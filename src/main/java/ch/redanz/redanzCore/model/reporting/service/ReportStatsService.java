package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.SpecialRegistrationRepo;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.SoldOutOverrideService.ItemType;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
import ch.redanz.redanzCore.model.reporting.response.ResponseStatsDanceRole;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReportStatsService {
  private final RegistrationService registrationService;
  private final BundleService bundleService;
  private final OutTextService outTextService;
  private final FoodService foodService;
  private final EventService eventService;
  private final BundleEventTrackService bundleEventTrackService;
  private final WorkflowStatusService workflowStatusService;
  private final DanceRoleService danceRoleService;
  private final SpecialRegistrationRepo specialRegistrationRepo;
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final FoodRegistrationRepo foodRegistrationRepo;
  private final DiscountRegistrationRepo discountRegistrationRepo;

  // blocks of the overview report
  public static final String BLOCK_MAIN = "MAIN";
  public static final String BLOCK_SPECIALS = "SPECIALS";
  public static final String BLOCK_FOOD = "FOOD";
  public static final String BLOCK_DISCOUNTS = "DISCOUNTS";

  // registrations and booked items are loaded once per event and counted in memory
  // (before: one count query per cell and dance role: slow with many registrations)
  private record Counter(List<DanceRole> danceRoles, Set<Long> submitted, Set<Long> confirming, Set<Long> done, Set<Long> cancelled, Set<Long> submittedConfirmingDone) {}

  public List<ResponseStats> getStatsReport(Language language, Event event) {
    String yes = outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_YES_EN.getOutTextKey(), language.getLanguageKey()).getOutText();
    String no = outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_NO_EN.getOutTextKey(), language.getLanguageKey()).getOutText();
    Long submitted = workflowStatusService.getSubmitted().getWorkflowStatusId();
    Long confirming = workflowStatusService.getConfirming().getWorkflowStatusId();
    Long done = workflowStatusService.getDone().getWorkflowStatusId();
    Counter counter = new Counter(
      danceRoleService.all(), Set.of(submitted), Set.of(confirming), Set.of(done),
      Set.of(workflowStatusService.getCancelled().getWorkflowStatusId()), Set.of(submitted, confirming, done)
    );

    // totals count participants only, the dance role split counts all active registrations (as before)
    List<Registration> registrations = registrationService.findAllActiveByEvent(event);
    List<Registration> participants = filter(registrations, registration -> registration.getRegistrationType() == RegistrationType.PARTICIPANT);
    List<SpecialRegistration> specialRegistrations = specialRegistrationRepo.findAllByRegistrationEvent(event);
    List<PrivateClassRegistration> privateClassRegistrations = privateClassRegistrationRepo.findAllByEventPrivateClassEvent(event);
    List<FoodRegistration> foodRegistrations = foodRegistrationRepo.findAllByRegistrationEvent(event);
    List<DiscountRegistration> discountRegistrations = discountRegistrationRepo.findAllByEventDiscountEvent(event);

    List<ResponseStats> stats = new ArrayList<>();
    stats.add(
      new ResponseStats(
        "Workshop"
        ,event.getName()
        ,countAndSplitRoles(counter, participants, registrations, counter.submittedConfirmingDone())
        ,countAndSplitRoles(counter, participants, registrations, counter.submitted())
        ,countAndSplitRoles(counter, participants, registrations, counter.confirming())
        ,countAndSplitRoles(counter, participants, registrations, counter.done())
        ,event.getCapacity()
        ,event.isSoldOut() ? yes : no
      ).withSoldOutOverride(ItemType.EVENT.name(), event.getEventId(), event.getSoldOutOverride(), event.isSoldOutComputed())
        .withCountCancelled(countAndSplitRoles(counter, participants, registrations, counter.cancelled()))
        .inBlock(BLOCK_MAIN)
    );

    eventService.findAllEventBundles(event).forEach(eventBundle -> {
      List<BundleEventTrack> bundleEventTracks = bundleEventTrackService.findAllByEventAndBundle(event, eventBundle.getBundle());

      bundleEventTracks.forEach(
        bundleEventTrack -> {
          Track track = bundleEventTrack.getEventTrack().getTrack();
          Bundle bundle = bundleEventTrack.getBundle();
          Predicate<Registration> inTrack = registration ->
            track != null
              && registration.getTrack() != null && Objects.equals(registration.getTrack().getTrackId(), track.getTrackId())
              && registration.getBundle() != null && Objects.equals(registration.getBundle().getBundleId(), bundle.getBundleId());
          List<Registration> trackParticipants = filter(participants, inTrack);
          List<Registration> trackRegistrations = filter(registrations, inTrack);
          ResponseStats trackStats =
            new ResponseStats(
              bundleEventTrack.getBundle().getName()
              , bundleEventTracks.size() > 1 ? bundle.getName() + " / " + track.getName() : bundle.getName()
              , countAndSplitRoles(counter, trackParticipants, trackRegistrations, counter.submittedConfirmingDone())
              , countAndSplitRoles(counter, trackParticipants, trackRegistrations, counter.submitted())
              , countAndSplitRoles(counter, trackParticipants, trackRegistrations, counter.confirming())
              , countAndSplitRoles(counter, trackParticipants, trackRegistrations, counter.done())
              , bundleEventTrack.getCapacity()
              , eventBundle.isSoldOut() || bundleEventTrack.isSoldOut() ? yes : no
            ).withSoldOutOverride(ItemType.BUNDLE_EVENT_TRACK.name(), bundleEventTrack.getBundleEventTrackId(), bundleEventTrack.getSoldOutOverride(), bundleEventTrack.isSoldOutComputed())
              // pass of the track, adjustable in the expanded row
              .withBundle(
                new ResponseStats("Pass", bundle.getName(), null, null, null, null, eventBundle.getCapacity(), eventBundle.isSoldOut() ? yes : no)
                  .withSoldOutOverride(ItemType.EVENT_BUNDLE.name(), eventBundle.getEventBundleId(), eventBundle.getSoldOutOverride(), eventBundle.isSoldOutComputed())
              )
              .withDanceRoles(
                bundleEventTrack.getBundleEventTrackDanceRoles().stream()
                  .map(bundleEventTrackDanceRole -> {
                    DanceRole danceRole = bundleEventTrackDanceRole.getEventDanceRole().getDanceRole();
                    return new ResponseStatsDanceRole(
                      danceRole.getDanceRoleId()
                      , danceRole.getName()
                      , bundleEventTrackDanceRole.getBundleEventTrackDanceRoleId()
                      , bundleEventTrackDanceRole.getSoldOutMode() == null ? null : bundleEventTrackDanceRole.getSoldOutMode().name()
                      , (bundleEventTrackDanceRole.isSoldOutComputed() ? DanceRoleSoldOutStatus.PARTNER_ONLY : DanceRoleSoldOutStatus.OPEN).name()
                      , bundleEventTrackDanceRole.getSoldOutStatus().name()
                    );
                  })
                  .sorted(Comparator.comparing(ResponseStatsDanceRole::getDanceRoleId))
                  .collect(Collectors.toList())
                , Boolean.TRUE.equals(track.getPartnerRequired()) || Boolean.TRUE.equals(track.getOwnPartnerRequired())
              )
              .withCountCancelled(countAndSplitRoles(counter, trackParticipants, trackRegistrations, counter.cancelled()))
              .inBlock(BLOCK_MAIN);
          trackStats.setTrack(track.getName());
          stats.add(trackStats);
        });
    });

    bundleService.getAllWithoutTracksByEvent(event).forEach(bundle -> {
      EventBundle eventBundle = eventService.findByEventAndBundle(event, bundle);
      Predicate<Registration> inBundle = registration -> registration.getBundle() != null && Objects.equals(registration.getBundle().getBundleId(), bundle.getBundleId());
      List<Registration> bundleParticipants = filter(participants, inBundle);
      List<Registration> bundleRegistrations = filter(registrations, inBundle);
      stats.add(
        new ResponseStats(
          "Simple Pass"
          ,bundle.getName()
          ,countAndSplitRoles(counter, bundleParticipants, bundleRegistrations, counter.submittedConfirmingDone())
          ,countAndSplitRoles(counter, bundleParticipants, bundleRegistrations, counter.submitted())
          ,countAndSplitRoles(counter, bundleParticipants, bundleRegistrations, counter.confirming())
          ,countAndSplitRoles(counter, bundleParticipants, bundleRegistrations, counter.done())
          ,eventBundle.getCapacity()
          ,eventBundle.isSoldOut() ? yes : no
        ).withSoldOutOverride(ItemType.EVENT_BUNDLE.name(), eventBundle.getEventBundleId(), eventBundle.getSoldOutOverride(), eventBundle.isSoldOutComputed())
          .withCountCancelled(countAndSplitRoles(counter, bundleParticipants, bundleRegistrations, counter.cancelled()))
          .inBlock(BLOCK_MAIN)
      );
    });

    // Event
    event.getEventSpecials().forEach(eventSpecial -> {
      if (!eventSpecial.getInfoOnly()) {
        List<Registration> specials = specialRegistrations.stream()
          .filter(specialRegistration -> Objects.equals(specialRegistration.getEventSpecial().getEventSpecialId(), eventSpecial.getEventSpecialId()))
          .map(SpecialRegistration::getRegistration)
          .collect(Collectors.toList());
        stats.add(
          new ResponseStats(
            "Special"
            , outTextService.getOutTextByKeyAndLangKey(eventSpecial.getName(), language.getLanguageKey()).getOutText()
            , countAndSplitRoles(counter, specials, specials, counter.submittedConfirmingDone())
            , countAndSplitRoles(counter, specials, specials, counter.submitted())
            , countAndSplitRoles(counter, specials, specials, counter.confirming())
            , countAndSplitRoles(counter, specials, specials, counter.done())
            , eventSpecial.getCapacity()
            , eventSpecial.getSoldOut() ? yes : no
          ).withSoldOutOverride(ItemType.EVENT_SPECIAL.name(), eventSpecial.getEventSpecialId(), eventSpecial.getSoldOutOverride(), eventSpecial.getSoldOutComputed())
            .withCountCancelled(countAndSplitRoles(counter, specials, specials, counter.cancelled()))
            .inBlock(BLOCK_SPECIALS)
        );
      }
    });

    foodService.getEventFoodSlots(event).forEach(eventFoodSlot -> {
      List<Registration> foods = foodRegistrations.stream()
        .filter(foodRegistration -> Objects.equals(foodRegistration.getEventFoodSlot().getEventFoodSlotId(), eventFoodSlot.getEventFoodSlotId()))
        .map(FoodRegistration::getRegistration)
        .collect(Collectors.toList());
      stats.add(
        new ResponseStats(
          "Food"
          , outTextService.getOutTextByKeyAndLangKey(eventFoodSlot.getName(), language.getLanguageKey()).getOutText()
          , countAsList(foods, counter.submittedConfirmingDone())
          , countAsList(foods, counter.submitted())
          , countAsList(foods, counter.confirming())
          , countAsList(foods, counter.done())
          , null
          , no
        ).withCountCancelled(countAsList(foods, counter.cancelled()))
          .inBlock(BLOCK_FOOD)
      );
    });

    // Track Discounts
//    Set<EventDiscount> eventDiscounts = new HashSet<>();
//    event.getEventTracks().forEach(eventTrack -> {
//      eventTrack.getTrack().getEventDiscounts().forEach(eventDiscount -> {
//        eventDiscounts.add(eventDiscount);
//      });
//    });
    event.getEventDiscounts().forEach(eventDiscount -> {
      List<Registration> discounts = discountRegistrations.stream()
        .filter(discountRegistration -> Objects.equals(discountRegistration.getEventDiscount().getEventDiscountId(), eventDiscount.getEventDiscountId()))
        .map(DiscountRegistration::getRegistration)
        .collect(Collectors.toList());
      stats.add(
        new ResponseStats(
          "Discount"
          , outTextService.getOutTextByKeyAndLangKey(eventDiscount.getName(), language.getLanguageKey()).getOutText()
          , countAsList(discounts, counter.submittedConfirmingDone())
          , countAsList(discounts, counter.submitted())
          , countAsList(discounts, counter.confirming())
          , countAsList(discounts, counter.done())
          , eventDiscount.getCapacity()
          , eventDiscount.getCapacity() == null ? no
            : count(discounts, counter.submittedConfirmingDone(), null) >= eventDiscount.getCapacity() ? yes : no
        ).withCountCancelled(countAsList(discounts, counter.cancelled()))
          .inBlock(BLOCK_DISCOUNTS)
      );
    });
    // Private Classes
    event.getEventPrivates().forEach(
      eventPrivateClass -> {
        List<Registration> privates = privateClassRegistrations.stream()
          .filter(privateClassRegistration -> Objects.equals(privateClassRegistration.getEventPrivateClass().getEventPrivateClassId(), eventPrivateClass.getEventPrivateClassId()))
          .map(PrivateClassRegistration::getRegistration)
          .collect(Collectors.toList());
        stats.add(
          new ResponseStats(
            "Private Class"
            ,eventPrivateClass.getName()
            ,countAndSplitRoles(counter, privates, privates, counter.submittedConfirmingDone())
            ,countAndSplitRoles(counter, privates, privates, counter.submitted())
            ,countAndSplitRoles(counter, privates, privates, counter.confirming())
            ,countAndSplitRoles(counter, privates, privates, counter.done())
            ,eventPrivateClass.getCapacity()
            ,eventPrivateClass.getSoldOut() ? yes : no
          ).withSoldOutOverride(ItemType.EVENT_PRIVATE_CLASS.name(), eventPrivateClass.getEventPrivateClassId(), eventPrivateClass.getSoldOutOverride(), eventPrivateClass.getSoldOutComputed())
            .withCountCancelled(countAndSplitRoles(counter, privates, privates, counter.cancelled()))
            .inBlock(BLOCK_SPECIALS)
        );
      });
    return stats;
  }

  private static List<Registration> filter(List<Registration> registrations, Predicate<Registration> predicate) {
    return registrations.stream().filter(predicate).collect(Collectors.toList());
  }

  // number of registrations in one of the workflow statuses (and of the dance role, if given)
  private static int count(List<Registration> registrations, Set<Long> workflowStatusIds, DanceRole danceRole) {
    return (int) registrations.stream()
      .filter(registration -> registration.getWorkflowStatus() != null && workflowStatusIds.contains(registration.getWorkflowStatus().getWorkflowStatusId()))
      .filter(registration -> danceRole == null
        || (registration.getDanceRole() != null && Objects.equals(registration.getDanceRole().getDanceRoleId(), danceRole.getDanceRoleId())))
      .count();
  }

  private static List<String> countAsList(List<Registration> registrations, Set<Long> workflowStatusIds) {
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(count(registrations, workflowStatusIds, null)));
    return count;
  }

  // [total, "Lead: 3, Follow: 2"] as the former count...AndSplitRoles methods
  private static List<String> countAndSplitRoles(Counter counter, List<Registration> totalRegistrations, List<Registration> roleRegistrations, Set<Long> workflowStatusIds) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(count(totalRegistrations, workflowStatusIds, null)));
    counter.danceRoles().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,count(roleRegistrations, workflowStatusIds, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  private static String formatCountToString(String formatedCount, int count, String pfx) {
    if (count > 0) {
      formatedCount = formatedCount.isBlank() ? "" : ", ";
      formatedCount = formatedCount + pfx + count;
      return formatedCount;
    }
    return "";
  }
}
