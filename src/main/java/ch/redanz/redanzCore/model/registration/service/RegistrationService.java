package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.*;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.service.email.EmailService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
  private final EventService eventService;
  private final WorkflowStatusService workflowStatusService;
  private final WorkflowTransitionService workflowTransitionService;
  private final RegistrationRepo registrationRepo;

  private final BundleService bundleService;
  private final TrackService trackService;
  private final DanceRoleService danceRoleService;
  private final PersonService personService;
  private final UserService userService;
  private final RegistrationMatchingService registrationMatchingService;
  private final SlotService slotService;
  private final FoodRegistrationRepo foodRegistrationRepo;
  private final HostRegistrationRepository hostRegistrationRepository;
  private final HosteeRegistrationRepository hosteeRegistrationRepository;
  private final HosteeSlotRegistrationRepository hosteeSlotRegistrationRepository;
  private final HosteeSleepUtilsRegistrationRepository hosteeSleepUtilsRegistrationRepository;
  private final HostSleepUtilRegistrationRepository hostSleepUtilRegistrationRepository;
  private final HostSlotRegistraitionRepository hostSlotRegistraitionRepository;
  private final SleepUtilService sleepUtilService;
  private final VolunteerRegistrationRepo volunteerRegistrationRepo;
  private final VolunteerSlotRegistrationRepo volunteerSlotRegistrationRepo;
  private final DonationRegistrationRepo donationRegistrationRepo;
  private final ScholarshipRegistrationRepo scholarshipRegistrationRepo;



  @Autowired
  private final Configuration mailConfig;
//  String loginLink;


  public List<Registration> findAll() {return registrationRepo.findAll(); }
  public List<WorkflowStatus> getWorkflowStatusList() {return workflowStatusService.findAll();}
  public void register(Registration registration) { registrationRepo.save(registration);}
  public Optional<Registration> findByParticipantAndEvent(Person participant, Event event) {
    return registrationRepo.findByParticipantAndEvent(participant, event);
  }

  public List<FoodRegistration> getFoodRegistrations(Registration registration) {
    return foodRegistrationRepo.findAllByFoodRegistrationIdRegistrationId(registration.getRegistrationId());
  }
  public Map<String, List<Object>> getHosteeRegistrations(Registration registration) {
    Map<String, List<Object>> hosteeRegistrationMap = new HashMap<>();

    HosteeRegistration hosteeRegistration = hosteeRegistrationRepository.findByRegistration(registration);
    List<Object> hosteeRegistrations = new ArrayList<>();
    hosteeRegistrations.add(hosteeRegistration);
    hosteeRegistrationMap.put(
      "hosteeRegistration"
      , hosteeRegistrations
    );

    hosteeRegistrationMap.put(
      "hosteeSleepUtilRegistration"
      ,Collections.singletonList(hosteeSleepUtilsRegistrationRepository.findAllByHosteeRegistration(hosteeRegistration))
    );

    hosteeRegistrationMap.put(
      "hosteeSlotRegistration"
      ,Collections.singletonList(hosteeSlotRegistrationRepository.findAllByHosteeRegistration(hosteeRegistration))
    );

    return hosteeRegistrationMap;
  }

  public Map<String, String> getScholarshipRegistration(Registration registration) {
    Map<String, String> scholarshipRegistrationMap = new HashMap<>();
    ScholarshipRegistration scholarshipRegistration = scholarshipRegistrationRepo.findByRegistration(registration);
    if (scholarshipRegistration != null) {
      scholarshipRegistrationMap.put(
        "intro"
        , scholarshipRegistration.getIntro()
      );
      return scholarshipRegistrationMap;
    } else {
      return null;
    }
  }

  public Map<String, String> getDonationRegistration(Registration registration) {
    Map<String, String> donationRegistrationMap = new HashMap<>();

    DonationRegistration donationRegistration = donationRegistrationRepo.findByRegistration(registration);
    if (donationRegistration != null) {

      donationRegistrationMap.put(
        "value"
        , String.valueOf(donationRegistration.getAmount())
      );
      return donationRegistrationMap;
    } else {
      return null;
    }
  }

  public Map<String, List<Object>> getVolunteerRegistration(Registration registration) {
    Map<String, List<Object>> volunteerRegistrationMap = new HashMap<>();

    VolunteerRegistration volunteerRegistration = volunteerRegistrationRepo.findByRegistration(registration);
    List<Object> volunteerRegistrations = new ArrayList<>();
    log.info("volunteerRegistration 1");

    log.info("volunteerRegistration null? : {}", volunteerRegistrations == null);

    if (volunteerRegistration != null) {

      volunteerRegistrations.add(volunteerRegistration);
      log.info("volunteerRegistration 2");
      volunteerRegistrationMap.put(
        "volunteerRegistration"
        ,volunteerRegistrations
      );
      log.info("volunteerRegistration 3");

      volunteerRegistrationMap.put(
        "volunteerSlotRegistration"
        , Collections.singletonList(volunteerSlotRegistrationRepo.findAllByVolunteerRegistration(volunteerRegistration))
      );
      log.info("volunteerRegistration 4");

      volunteerRegistrationMap.put(
        "mobile",
        Collections.singletonList(volunteerRegistration.getRegistration().getParticipant().getMobile())
      );
      log.info("volunteerRegistration 5");

      return volunteerRegistrationMap;
    } else {
      return null;
    }
  }

  public Map<String, List<Object>> getHostRegistrations(Registration registration) {
    Map<String, List<Object>> hostRegistrationMap = new HashMap<>();

    HostRegistration hostRegistration = hostRegistrationRepository.findAllByRegistration(registration);
    List<Object> hostRegistrations = new ArrayList<>();
    hostRegistrations.add(hostRegistration);
    hostRegistrationMap.put(
       "hostRegistration"
       , hostRegistrations
    );

    hostRegistrationMap.put(
      "hostSleepUtilRegistration"
     , Collections.singletonList(hostSleepUtilRegistrationRepository.findAllByHostRegistration(hostRegistration))
    );

    hostRegistrationMap.put(
      "hostSlotRegistration"
      , Collections.singletonList(hostSlotRegistraitionRepository.findAllByHostRegistration(hostRegistration))
    );

    return hostRegistrationMap;
  }

  public List<Registration> getAllSubmittedRegistrations(){
    List<Registration> submittedRegistrations = new ArrayList<>();

    registrationRepo.findAllByEvent(
      eventService.findByName(EventConfig.EVENT2022.getName())
    ).forEach(registration -> {
      log.info("inc@registrationService, registration of {}", registration.getParticipant().getFirstName());
      if (workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration)
        .getWorkflowStatus().getName()
        .equals(WorkflowStatusConfig.SUBMITTED.getName()
        )
      ) {
        submittedRegistrations.add(registration);
      }
    });
    return submittedRegistrations;
  }

//  @Transactional
  public void submitRegistration(Long userId, JsonObject request, String link) throws IOException, TemplateException {
    RegistrationRequest registrationRequest= new RegistrationRequest(
            request.get("eventId").getAsLong(),
            request.get("bundleId").getAsLong(),
            request.get("trackId").isJsonNull() ? null : request.get("trackId").getAsLong(),
            request.get("danceRoleId").isJsonNull() ? null : request.get("danceRoleId").getAsLong() ,
            request.get("partnerEmail").isJsonNull() ? null : request.get("partnerEmail").getAsString()
    );

    Registration registration = saveRegistration(registrationRequest, userId);

    if (!request.get("foodRegistration").getAsJsonArray().isEmpty()) {
      saveFoodRegistration(registration, request.get("foodRegistration").getAsJsonArray());
    }
    if (!request.get("hostRegistration").getAsJsonArray().isEmpty()) {
      saveHostRegistration(registration, request.get("hostRegistration").getAsJsonArray());
    }
    if (!request.get("hosteeRegistration").getAsJsonArray().isEmpty()) {
      saveHosteeRegistration(registration, request.get("hosteeRegistration").getAsJsonArray());
    }
    if (request.get("volunteerRegistration") != null) {
      saveVolunteerRegistration(registration, request.get("volunteerRegistration").getAsJsonObject());
    }
    if (request.get("scholarshipRegistration") != null) {
      saveScholarishpRegistration(registration, request.get("scholarshipRegistration").getAsJsonArray());
    }
    if (request.get("donationRegistration") != null) {
      saveDonationRegistration(registration, request.get("donationRegistration").getAsJsonArray());
    }
    saveWorkflowStatus(registration);
    doMatching(registration, registrationRequest);
    sendEmailConfirmation(registration, mailConfig, link, userId);
  }

  private void saveScholarishpRegistration(Registration registration, JsonArray scholarshipRegistration) {
    scholarshipRegistrationRepo.save(
      new ScholarshipRegistration(
        registration,
        scholarshipRegistration.get(0).getAsJsonObject().get("intro").getAsString()
      )
    );
  }
  private void saveDonationRegistration(Registration registration, JsonArray donationRegistration) {
    donationRegistrationRepo.save(
      new DonationRegistration(
        registration,
        donationRegistration.get(0).getAsJsonObject().get("amount").getAsInt()
      )
    );
  }
  private void saveVolunteerRegistration(Registration registration, JsonObject volunteerRegistration) {
    log.info("inc volunteerRegistration: {}", volunteerRegistration);
    String intro = volunteerRegistration.get("intro").isJsonNull() ? null : volunteerRegistration.get("intro").getAsString();
    log.info("inc introJson: {}", intro);
    String mobile = volunteerRegistration.get("mobile").isJsonNull() ? null : volunteerRegistration.get("mobile").getAsString();
    log.info("inc mobileJson: {}", mobile);
    JsonArray slotsJson = volunteerRegistration.get("slots").getAsJsonArray();
    log.info("inc slotsJson: {}", slotsJson);

    // volunteer registration
    volunteerRegistrationRepo.save(
      new VolunteerRegistration(
        registration,
        intro
      )
    );

    // slots
    slotsJson.forEach(slot -> {
      volunteerSlotRegistrationRepo.save(
        new VolunteerSlotRegistration(
          volunteerRegistrationRepo.findByRegistration(registration),
          slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
        )
      );
    });

    // add mobile
    Person person = personService.findByPersonId(registration.getParticipant().getPersonId());
    person.setMobile(mobile);
    personService.savePerson(person);
  }

  private void saveHosteeRegistration(Registration registration, JsonArray hosteeRegistration) {
    JsonObject slotsJson = hosteeRegistration.get(0).getAsJsonObject();
    JsonObject isShareRoomsJson = hosteeRegistration.get(1).getAsJsonObject();
    JsonObject nameRoomMateJson = hosteeRegistration.get(2).getAsJsonObject();
    JsonObject isSharedBedJson = hosteeRegistration.get(3).getAsJsonObject();
    JsonObject sleepUtilsJson = hosteeRegistration.get(4).getAsJsonObject();
    JsonObject hosteeCommentJson = hosteeRegistration.get(5).getAsJsonObject();

    // host registration
    hosteeRegistrationRepository.save(
        new HosteeRegistration(
                registration,
          !isShareRoomsJson.get("isShareRooms").isJsonNull() && isShareRoomsJson.get("isShareRooms").getAsBoolean(),
          nameRoomMateJson.get("nameRoomMate").isJsonNull() ? null : nameRoomMateJson.get("nameRoomMate").getAsString(),
          !isSharedBedJson.get("isSharedBed").isJsonNull() && isSharedBedJson.get("isSharedBed").getAsBoolean(),
          hosteeCommentJson.get("hosteeComment").isJsonNull() ? null : hosteeCommentJson.get("hosteeComment").getAsString()
        )
    );

    // hostee sleep util registration
    if (!sleepUtilsJson.get("sleepUtils").isJsonNull()) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {

        if (sleepUtil.getAsJsonObject().get("checked") != null && sleepUtil.getAsJsonObject().get("checked").getAsBoolean()) {
          hosteeSleepUtilsRegistrationRepository.save(
            new HosteeSleepUtilRegistration(
              hosteeRegistrationRepository.findByRegistration(registration),
              sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong())
            )
          );
        }
      });
    }

    // hostee slot registration
    if (!slotsJson.get("slots").isJsonNull()) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
        hosteeSlotRegistrationRepository.save(
                  new HosteeSlotRegistration(
                          hosteeRegistrationRepository.findByRegistration(registration),
                          slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
                  )
          );
      });
    }
  }

  private void saveHostRegistration(Registration registration, JsonArray hostRegistration) {
    JsonObject slotsJson = hostRegistration.get(0).getAsJsonObject();
    JsonObject personCountJson = hostRegistration.get(1).getAsJsonObject();
    JsonObject sleepUtilsJson = hostRegistration.get(2).getAsJsonObject();
    JsonObject hostCommentJson = hostRegistration.get(3).getAsJsonObject();

    log.info("hostRegistration, hostCommentJson: {}", hostCommentJson);

    // host registration
    hostRegistrationRepository.save(
        new HostRegistration(
                registration,
                personCountJson.get("personCount").isJsonNull() ? 0 : personCountJson.get("personCount").getAsInt(),
                hostCommentJson.get("hostComment").isJsonNull() ? null : hostCommentJson.get("hostComment").getAsString()
        )
    );

    // host sleep util registration
    if (!sleepUtilsJson.get("sleepUtils").isJsonNull()) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {
        hostSleepUtilRegistrationRepository.save(
          new HostSleepUtilRegistration(
            hostRegistrationRepository.findAllByRegistration(registration),
            sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong()),
            sleepUtil.getAsJsonObject().get("count").getAsInt()
          )
        );
      });
    }

    // host slot registration
    if (!slotsJson.get("slots").isJsonNull()) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
          hostSlotRegistraitionRepository.save(
                  new HostSlotRegistration(
                          hostRegistrationRepository.findAllByRegistration(registration),
                          slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
                  )
          );
      });
    }
  }

  private void saveFoodRegistration(Registration registration, JsonArray foodRegistration) {
    JsonArray foodSlotsJson = foodRegistration.get(0).getAsJsonObject().get("food").getAsJsonArray();
    foodSlotsJson.forEach(foodSlot -> {
      foodRegistrationRepo.save(
          new FoodRegistration(
                  new FoodRegistrationId(
                          registration.getRegistrationId(),
                          foodSlot.getAsJsonObject().get("food").getAsJsonObject().get("foodId").getAsLong(),
                          foodSlot.getAsJsonObject().get("slot").getAsJsonObject().get("slotId").getAsLong()
                  )
          )
      );
    });
  }


  private void saveVolunteerRegistration(RegistrationRequest request) {
  }

  private void validateRegistrationRequest(RegistrationRequest request){
    // validate here
  }
  private Registration saveRegistration(RegistrationRequest request, Long userId){
    Registration newRegistration = new Registration(
            eventService.findByEventId(request.getEventId()),
            bundleService.findByBundleId(request.getBundleId()),
            personService.findByUser(userService.findByUserId(userId))
    );

    if (request.getTrackId() != null) {
      newRegistration.setTrack(trackService.findByTrackId(request.getTrackId()));
    }

    if (request.getDanceRoleId() != null ) {
      newRegistration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    }

    registrationRepo.save(newRegistration);
    return newRegistration;
  }

  private void saveWorkflowStatus(Registration registration) {
    WorkflowTransition workflowTransition = new WorkflowTransition(
            workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.SUBMITTED.getName()),
            registration,
            LocalDateTime.now()
    );
    workflowTransitionService.saveWorkflowTransition(workflowTransition);
  }

  private void doMatching(Registration registration, RegistrationRequest request) {
    if (request.getTrackId() != null && registration.getTrack().getPartnerRequired()) {
      RegistrationMatching registrationMatching
              = new RegistrationMatching(registration);

      if (request.getPartnerEmail() != null) {
        registrationMatching.setPartnerEmail(String.valueOf(request.getPartnerEmail()));
      }
      registrationMatchingService.save(registrationMatching);
    }
  }

  private void sendEmailConfirmation(
          Registration registration,
          Configuration mailConfig,
          String link,
          Long userId
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("link", link);
    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationSubmitted.ftl");

//    EmailService emailService = new EmailService();
    EmailService.sendEmail(
            EmailService.getSession(),
            userService.findByUserId(userId).getEmail(),
            "Registration Submitted",
            FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    );
  }
}
