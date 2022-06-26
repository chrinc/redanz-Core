package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.*;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.service.email.EmailService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
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
import org.springframework.transaction.annotation.Transactional;
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
  private final LanguageRepo languageRepo;
  private final OutTextService outTextService;



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

  public void doMatch(Registration registration1, Registration registration2){
    log.info("inc, bfr match 1");
    RegistrationMatching registrationMatching1 = registrationMatchingService.findByRegistration1(registration1).get();
    registrationMatching1.setRegistration2(registration2);

    log.info("inc, bfr match 2");
    RegistrationMatching registrationMatching2 = registrationMatchingService.findByRegistration1(registration2).get();
    registrationMatching2.setRegistration2(registration1);

    log.info("inc, bfr save ");
    registrationMatchingService.save(registrationMatching1);
    registrationMatchingService.save(registrationMatching2);

    log.info("inc, done");
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

  @Transactional
  public void submitRegistration(Long userId, JsonObject request, String link) throws IOException, TemplateException {
      log.info("request: {}", request);
      RegistrationRequest registrationRequest = new RegistrationRequest(
        request.get("eventId").getAsLong(),
        request.get("bundleId").getAsLong(),
        request.get("trackId")== null ? null : request.get("trackId").getAsLong(),
        request.get("danceRoleId") == null ? null : request.get("danceRoleId").getAsLong(),
        request.get("partnerEmail")== null ? null : request.get("partnerEmail").getAsString()
      );

      Registration registration = saveRegistration(registrationRequest, userId);

      if (!request.get("foodRegistration").getAsJsonArray().isEmpty()) {
        saveFoodRegistration(registration, request.get("foodRegistration").getAsJsonArray());
      }

    log.info("inc, hostRegistration: {}", request.get("hostRegistration"));
      if (!request.get("hostRegistration").getAsJsonArray().isEmpty()) {
        saveHostRegistration(registration, request.get("hostRegistration").getAsJsonArray());
      }
    log.info("inc, hosteeRegistration: {}", request.get("hosteeRegistration"));

      if (!request.get("hosteeRegistration").getAsJsonArray().isEmpty()) {
        saveHosteeRegistration(registration, request.get("hosteeRegistration").getAsJsonArray());
      }
      log.info("inc, volunteerRegistration: {}", request.get("volunteerRegistration"));
      if (request.get("volunteerRegistration") != null) {
        saveVolunteerRegistration(registration, request.get("volunteerRegistration").getAsJsonObject());
      }

      if (request.get("scholarshipRegistration") != null) {
        saveScholarishpRegistration(registration, request.get("scholarshipRegistration").getAsJsonArray());
      }

      if (!request.get("donationRegistration").isJsonNull()) {
        saveDonationRegistration(registration, request.get("donationRegistration").getAsJsonArray());
      }
      saveWorkflowStatus(registration);
      doMatching(registration, registrationRequest);
      log.info("inc, bfr send email ");
      sendEmailConfirmation(registration, mailConfig, link, userId);
  }
  public Registration getRegistration(Long userId, Event event) {
    return registrationRepo.findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      event
    ).get();
  }

  public RegistrationResponse getRegistrationResponse(Long userId, Long eventId) {
    Optional<Registration> registrationOptional =
      findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.findByEventId(eventId)
      );

    if (registrationOptional.isPresent()) {
      Registration registration = registrationOptional.get();
      RegistrationResponse registrationResponse = new RegistrationResponse(
        registration.getRegistrationId(),
        registration.getParticipant().getUser().getUserId(),
        registration.getEvent().getEventId(),
        registration.getBundle().getBundleId()
      );

      registrationResponse.setEventId(registration.getEvent().getEventId());
      registrationResponse.setRegistrationId(registration.getRegistrationId());
      registrationResponse.setBundleId(registration.getBundle().getBundleId());

      // track
      if (registration.getTrack() != null) {
        registrationResponse.setTrackId(registration.getTrack().getTrackId());
      }

      // dance role
      if (registration.getDanceRole() != null) {
        registrationResponse.setDanceRoleId(registration.getDanceRole().getDanceRoleId());
      }

      // partner Email
      RegistrationMatching registrationMatching = registrationMatchingService.findByRegistration1(registration).orElse(null);
      if (registrationMatching != null) {
        registrationResponse.setPartnerEmail(registrationMatching.getPartnerEmail());
      }

      // workflow Status
      WorkflowTransition workflowTransition = workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration);
      registrationResponse.setWorkflowStatus(workflowTransition.getWorkflowStatus());

      // Food Registration
      registrationResponse.setFoodRegistrations(
        getFoodRegistrations(registration)
      );

      // Host Registration
      registrationResponse.setHostRegistration(
        getHostRegistrations(registration)
      );

      // Hostee Registration
      registrationResponse.setHosteeRegistration(
        getHosteeRegistrations(registration)
      );

      // Volunteer Registration
      registrationResponse.setVolunteerRegistration(
        getVolunteerRegistration(registration)
      );

      // Scholarship Registration
      registrationResponse.setScholarshipRegistration(
        getScholarshipRegistration(registration)
      );

      // Scholarship Registration
      registrationResponse.setDonationRegistration(
        getDonationRegistration(registration)
      );
      return registrationResponse;
    }
    return null;
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
          hosteeCommentJson.get("hosteeComment") == null ? null : hosteeCommentJson.get("hosteeComment").getAsString()
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

    log.info("hostRegistration, hostCommentJson null?: {}", hostCommentJson.get("hostComment") == null);

    // host registration
    hostRegistrationRepository.save(
        new HostRegistration(
                registration,
                personCountJson.get("personCount").isJsonNull() ? 0 : personCountJson.get("personCount").getAsInt(),
                hostCommentJson.get("hostComment") == null ? "" : hostCommentJson.get("hostComment").getAsString()
        )
    );
    log.info("hostRegistration, sleep utils null ?: {}", sleepUtilsJson.get("sleepUtils") == null);
    log.info("hostRegistration, sleep utils null ?: {}", sleepUtilsJson.get("sleepUtils").getAsJsonArray());

    // host sleep util registration
    if (sleepUtilsJson.get("sleepUtils") != null) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {
        if (sleepUtil.getAsJsonObject().get("count") != null) {
          hostSleepUtilRegistrationRepository.save(
            new HostSleepUtilRegistration(
              hostRegistrationRepository.findAllByRegistration(registration),
              sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong()),
              sleepUtil.getAsJsonObject().get("count").getAsInt()
            )
          );
        };
      });
    }

    log.info("hostRegistration, slots null?: {}", slotsJson.get("slots") == null);
    // host slot registration
    if (slotsJson.get("slots") != null) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
          hostSlotRegistraitionRepository.save(
                  new HostSlotRegistration(
                          hostRegistrationRepository.findAllByRegistration(registration),
                          slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
                  )
          );
      });
    }

    log.info("all done" );
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

    log.info("(registration. track?: {}", request);
    log.info("(registration. request.getTrackId()?: {}", request.getTrackId());
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

  private void sendConfirmingReminder() {
//    workflowStatusService..
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

    String languageKey =
      registration.getParticipant().getPersonLang() == null ?
        languageRepo.findLanguageByLanguageKey("GE").getLanguageKey() :
        registration.getParticipant().getPersonLang().getLanguageKey();

    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SUBMITTED_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("base",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SUBMITTED_BASE_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("find_details_01",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SUBMITTED_DETAILS01_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    log.info("inc, bfr send email");
    EmailService.sendEmail(
      EmailService.getSession(),
      userService.findByUserId(userId).getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    );
  }

  private void sendEmailBookingConfirmation(
          Person person,
          Configuration mailConfig
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("firstName", person.getFirstName());
    Template template = mailConfig.getTemplate("registrationDone.ftl");

    String languageKey =
      person.getPersonLang() == null ?
        languageRepo.findLanguageByLanguageKey("GE").getLanguageKey() :
        person.getPersonLang().getLanguageKey();

    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("base",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_BASE_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    log.info("inc, bfr send email");
    EmailService.sendEmail(
      EmailService.getSession(),
      person.getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    );
  }

  public void onPaymentReceived(Long userId) throws IOException, TemplateException {
    workflowTransitionService.saveWorkflowTransition(
      new WorkflowTransition(
        workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.DONE.getName()),
        findByParticipantAndEvent(
          personService.findByUser(userService.findByUserId(userId)),
          eventService.findByName(EventConfig.EVENT2022.getName())
        ).get(),
        LocalDateTime.now()
      )
    );
    sendEmailBookingConfirmation(personService.findByUser(userService.findByUserId(userId)), mailConfig);
  }

  public void onPaymentConfirmed(JsonObject request) throws IOException, TemplateException {
    JsonObject transaction = request.get("transaction").getAsJsonObject();
    log.info("inc@onPaymentConfirmed, transaction: {}", transaction);
    Long userId = transaction.get("referenceId").getAsLong();
    Number amount = transaction.get("invoice").getAsJsonObject().get("amount").getAsNumber();
    log.info("inc@onPaymentConfirmed, userId: {}", userId);
    log.info("inc@onPaymentConfirmed, amount: {}", amount);
    //    @todo check amount first
    onPaymentReceived(userId);
  }
}
