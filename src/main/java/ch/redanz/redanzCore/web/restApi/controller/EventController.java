package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.exception.HasRegistrationException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("core-api/app/event")
@AllArgsConstructor
public class EventController {
  private final EventService eventService;
  private final TrackService trackService;
  private final SlotService slotService;
  private final OutTextService outTextService;
  private final AccommodationService accommodationService;
  private final SpecialService specialService;
  private final BundleService bundleService;
  private final PrivateClassService privateClassService;
  private final VolunteeringService volunteeringService;
  private final ScholarshipService scholarshipService;
  private final VolunteerService volunteerService;
  private final PersonService personService;
  private final FoodService foodService;
  private final DiscountService discountService;
  private final EventRegistrationService eventRegistrationService;
  private final EventTypeSlotService eventTypeSlotService;
  private final EventPartService eventPartService;

  @GetMapping(path = "/schema/event")
  public List<Map<String, String>> getEventSchema() {
//    log.info("inc@getEventSchema");
    return eventService.getEventSchema();
  }

  @GetMapping(path = "/data/event")
  public List<Map<String, String>> getEventData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getEventsData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/data/event/all")
  public List<Map<String, String>> getAllEventData() {
    return eventService.getEventsData(eventService.getAllEvents());
  }

  @GetMapping(path = "/schema/bundle")
  public List<Map<String, String>> getBundleSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getBundleSchema(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/data/bundle")
  public List<Map<String, String>> getBundleData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getBundlesData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/schema/eventPartInfo")
  public List<Map<String, String>> getEventPartInfoSchema(
    @RequestParam("eventId") Long eventId,
    @RequestParam("eventPartKey") String eventPartKey
  ) {
    log.info(eventPartKey);
    log.info(eventId.toString());
    EventPartInfo eventPartInfo = eventPartService.findByEventAndEventPart(
      eventService.findByEventId(eventId)
      ,eventPartService.findByKey(eventPartKey)
    );
    return eventPartService.getEventPartInfoSchema(eventPartInfo);
  }

  @GetMapping(path = "/data/eventPartInfo")
  public List<Map<String, String>> getEventPartInfoData(
    @RequestParam("eventId") Long eventId,
    @RequestParam("eventPartKey") String eventPartKey
  ) {
//    log.info("evnPartData");
//    log.info("eventPartKey");
    log.info(eventPartKey);
    return eventPartService.getEventPartInfoData(
      eventPartService.findByEventAndEventPart(eventService.findByEventId(eventId)
        , eventPartService.findByKey(eventPartKey)
      )
    );
  }


  @GetMapping(path = "/schema/track")
  public List<Map<String, String>> getTrackSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return trackService.getTrackSchema(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/schema/trackDanceRole")
  public List<Map<String, String>> getTrackDanceRoleSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return trackService.getTrackDanceRoleSchema();
  }

  @GetMapping(path = "/data/trackDanceRole")
  public List<Map<String, String>> getTrackDanceRoleData(
    @RequestParam("trackId") Long trackId,
    @RequestParam("eventId") Long eventId
  ) {
    return trackService.getTrackDanceRoleData(trackService.findByTrackId(trackId));
  }


  @GetMapping(path = "/data/track")
  public List<Map<String, String>> getTrackData(
    @RequestParam("eventId") Long eventId
  ) {
    return trackService.getTracksData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/schema/foodSlot")
  public List<Map<String, String>> getFoodSlotSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return foodService.getFoodSlotSchema();
  }

  @GetMapping(path = "/data/foodSlot")
  public List<Map<String, String>> getFoodSlotData(
    @RequestParam("eventId") Long eventId
  ) {
    return foodService.getFoodData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/schema/eventPrivate")
  public List<Map<String, String>> getEventPrivateSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.eventPrivateSchema();
  }

  @PostMapping(path = "/eventPrivate/delete")
  @Transactional
  public void deleteEventPrivate(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject privateObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.deleteEventPrivate(
        privateObject
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping(path = "/schema/eventSpecial")
  public List<Map<String, String>> getEventSpecialeSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.eventSpecialSchema();
  }

  @GetMapping(path = "/schema/eventDiscount")
  public List<Map<String, String>> getEventDiscountSchema(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.eventDiscountSchema();
  }

  @PostMapping(path = "/eventSpecial/delete")
  @Transactional
  public void deleteEventSpecial(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject specialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.deleteEventSpecial(
        specialObject
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/eventDiscount/delete")
  @Transactional
  public void deleteEventDiscount(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject specialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.deleteEventDiscount(
        specialObject
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/trackDanceRole/delete")
  @Transactional
  public void deleteTrackDanceRole(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject specialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      trackService.deleteTrackDanceRole(
        specialObject
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }


  @PostMapping(path = "/eventPrivate/upsert")
  @Transactional
  public void upsertEventPrivate(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonEventPrivateObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.updateEventPrivate(
        jsonEventPrivateObject,
        eventService.findByEventId(jsonEventPrivateObject.get("eventId").getAsLong())
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/eventSpecial/upsert")
  @Transactional
  public void upsertEventSpecial(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonEventSpecialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.updateEventSpecial(
        jsonEventSpecialObject,
        eventService.findByEventId(jsonEventSpecialObject.get("eventId").getAsLong())
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/trackDanceRole/upsert")
  @Transactional
  public void upsertTrackDanceRole(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonEventSpecialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      trackService.updateTrackDanceRole(
        jsonEventSpecialObject
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }
  @PostMapping(path = "/eventDiscount/upsert")
  @Transactional
  public void upsertEventDiscount(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonEventDiscountObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.updateEventDiscount(
        jsonEventDiscountObject,
        eventService.findByEventId(jsonEventDiscountObject.get("eventId").getAsLong())
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping(path = "/data/eventPrivate")
  public List<Map<String, String>> getEventPrivateData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.eventPrivateData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/data/eventSpecial")
  public List<Map<String, String>> getEventSpecialData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.eventSpecialData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/data/eventDiscount")
  public List<Map<String, String>> getEventDiscountData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.eventDiscountData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/schema/hosting")
  public List<Map<String, String>> getHostingSchema(
    @RequestParam("eventId") Long eventId) {
    return accommodationService.getHostingSchema(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/data/hosting")
  public List<Map<String, String>> getHostingData(
    @RequestParam("eventId") Long eventId
  ) {
    return accommodationService.getHostingData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/schema/volunteering")
  public List<Map<String, String>> getVolunteerSchema(
    @RequestParam("eventId") Long eventId) {
    return volunteeringService.getVolunteerSchema();
  }

  @GetMapping(path = "/data/volunteering")
  public List<Map<String, String>> getVolunteerData(
    @RequestParam("eventId") Long eventId
  ) {
    return volunteeringService.getVolunteerData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/schema/requireTerms")
  public List<Map<String, String>> getTermsSchema(
    @RequestParam("eventId") Long eventId) {
    return eventService.getTermsSchema();
  }

  @GetMapping(path = "/data/requireTerms")
  public List<Map<String, String>> getTermsData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getTermsData(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/schema/scholarship")
  public List<Map<String, String>> getScholarshipSchema() {
    return scholarshipService.getScholarshipSchema();
  }

  @GetMapping(path = "/data/scholarship")
  public List<Map<String, String>> getScholarshipData(
    @RequestParam("eventId") Long eventId
  ) {
    return scholarshipService.getScholarshipData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/all")
  public List<Event> getAllEvents() {
//    log.info(eventService.getAllEvents().toString());
    return eventService.getAllEvents();
  }

  @GetMapping(path = "/byId")
  @Transactional
  public Event getEventById(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      return eventService.getById(eventId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

//  @GetMapping(path = "/current")
//  public Event getCurrentEvent() {
//    return eventService.getCurrentEvent();
//  }

  @GetMapping(path = "/active")
  public List<Event> getActivEvents() {
    return eventService.getActiveEvents();
  }

  @GetMapping(path = "/inactive")
  public List<Event> getInactiveEvents() {
    return eventService.getInactiveEvents();
  }

  @GetMapping(path = "/out-text/all")
  public HashMap getOutText() {
    ArrayList<String> types = new ArrayList<>() {
      {
        add("FRONT_LOGIN");
        add("FRONT_BASE");
      }
    };

    return outTextService.getOutTextByType(types);
  }

  @GetMapping(path = "/out-text/front-login-map")
  public List<Map<String, String>> getEtitableOutText() {
    ArrayList<String> types = new ArrayList<>() {
      {
        add("FRONT_LOGIN");
      }
    };

    return outTextService.getOutTextMapByType(types);
  }

  @GetMapping(path = "/slots/all")
  public List<Slot> getAllSlots(
    @RequestParam("eventId") Long eventId
  ) {
    return slotService.getAllSlots(eventService.findByEventId(eventId));
  }
  @GetMapping(path = "/slots/party")
  public List<Slot> getAllPartySlots(
    @RequestParam("eventId") Long eventId
  ) {
    return slotService.getAllSlots("party", eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/volunteer/slot/all")
  public List<Slot> getAllVolunteerSlots(
    @RequestParam("eventId") Long eventId
  ) {
    return slotService.getAllVolunteerSlots(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/volunteer/type/all")
  public List<VolunteerType> getAllVolunteerTypes(
    @RequestParam("eventId") Long eventId
  ) {
    return volunteerService.getAllVolunteerTypes(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/accommodation/all")
  public AccommodationResponse getAccommodationSlots(
    @RequestParam("eventId") Long eventId
  ) {
    return accommodationService.getResponse(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/eventSpecials")
  public List<EventSpecial> getEventSpecials(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.findSpecialsByEvent(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/bundleSpecials")
  public Set<EventSpecial> getBundleSpecials(
    @RequestParam("bundleId") Long bundleId
  ) {
    return bundleService.findSpecialsByBundle(bundleService.findByBundleId(bundleId));
  }

  @GetMapping(path = "/privateClasses/all")
  public List<EventPrivateClass> getPrivateClasses(
    @RequestParam("bundleId") Long bundleId,
    @RequestParam("eventId") Long eventId
  ) {
    List<EventPrivateClass> privateClasses;
    Event event = eventService.findByEventId(eventId);
    if (bundleId == null) {
      privateClasses =  eventService.findPrivateClassesByEvent(event);
    } else {
      privateClasses =  eventService.findPrivateClassesByEventAndBundle(event, bundleService.findByBundleId(bundleId));
    }
    return privateClasses;
  }

  @GetMapping (path = "/person/organizer")
  @Transactional
  public List<Person> getCheckInSlots(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      return personService.getAlOrganizers(event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/event/upsert")
  @Transactional
  public void upsertEvent(
    @RequestBody String jsonObject
  ) {
    try {
      eventService.updateEvent(
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/event/delete")
  @Transactional
  public void deleteEvent(
    @RequestBody String jsonObject
  ) {
    try {
      // update
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long eventId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Event event = eventService.findByEventId(eventId);
      if (eventRegistrationService.hasActiveRegistration(event)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_REGISTRATION_GE.getOutTextKey());
      } else if (eventRegistrationService.hasRegistration(event)) {
        eventService.archiveEvent(event);
      } else {
        eventService.deleteEvent(event);
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/bundle/upsert")
  @Transactional
  public void upsertBundle(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonBundleObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      bundleService.updateBundle(
        jsonBundleObject,
        eventService.findByEventId(jsonBundleObject.get("eventId").getAsLong())
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/bundle/delete")
  @Transactional
  public void deleteBundle(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject bundleObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      Bundle bundle = bundleService.findByBundleId(bundleObject.get("id").getAsLong());
      eventService.delete(
        eventService.findByEventAndBundle(
          eventService.findByEventId(bundleObject.get("eventId").getAsLong())
          ,bundle
        )
      );

      bundleService.deleteBundle(
        bundle
      );


    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/track/upsert")
  @Transactional
  public void upsertTrack(
    @RequestBody String jsonObject
  ) {

    JsonObject jsonTrackObject = JsonParser.parseString(jsonObject).getAsJsonObject();
    try {
      trackService.updateTrack(
        jsonTrackObject,
        eventService.findByEventId(jsonTrackObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/eventPartInfo/upsert")
  @Transactional
  public void upsertEventInfo(
    @RequestBody String jsonObject
  ) {
    JsonObject jsonTrackObject = JsonParser.parseString(jsonObject).getAsJsonObject();
    try {
      eventService.updateEventPartInfo(
        jsonTrackObject
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/track/delete")
  @Transactional
  public void deleteTrack(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Track track = trackService.findByTrackId(request.get("id").getAsLong());
      Event event = eventService.findByEventId(request.get("eventId").getAsLong());
      eventService.delete(event, track);
//      trackService.delete(track);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/foodSlot/upsert")
  @Transactional
  public void upsertFoodSlot(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonFoodObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.updateEventFoodSlot(
        jsonFoodObject,
        eventService.findByEventId(jsonFoodObject.get("eventId").getAsLong())
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/foodSlot/delete")
  @Transactional
  public void deleteFoodSlot(
    @RequestBody String jsonObject
  ) {
    try {
      // update
      JsonObject jsonFoodObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.deleteEventFoodSlot(
        jsonFoodObject
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/discount/upsert")
  @Transactional
  public void upsertDiscount(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long id = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
//      log.info(id.toString());
      if (id != null && id != 0 && eventRegistrationService.discountIsUsedAndHasRegistration(discountService.findByDiscountId(id))) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_SAVE_GE.getOutTextKey());
      } else {
        discountService.update(request);
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/hosting/upsert")
  @Transactional
  public void upsertHosting(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonHostingObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      accommodationService.updateHosting(
        jsonHostingObject,
        eventService.findByEventId(jsonHostingObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/hosting/delete")
  @Transactional
  public void deleteHosting(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonHostingObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      accommodationService.deleteHosting(
        jsonHostingObject,
        eventService.findByEventId(jsonHostingObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/volunteering/upsert")
  @Transactional
  public void upsertVolunteer(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonVolunteerObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      volunteeringService.updateVolunteering(
        jsonVolunteerObject,
        eventService.findByEventId(jsonVolunteerObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/volunteering/delete")
  @Transactional
  public void deleteVolunteering(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonVolunteerObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      volunteeringService.deleteVolunteering(
        jsonVolunteerObject,
        eventService.findByEventId(jsonVolunteerObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }


  @PostMapping(path = "/scholarship/upsert")
  @Transactional
  public void upsertScholarship(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonScholarshipObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      scholarshipService.updateScholarship(
        jsonScholarshipObject,
        eventService.findByEventId(jsonScholarshipObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/scholarship/delete")
  @Transactional
  public void deleteScholarship(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonScholarshipObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      scholarshipService.deleteScholarship(
        jsonScholarshipObject,
        eventService.findByEventId(jsonScholarshipObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping(path = "/event/clone")
  @Transactional
  public void cloneEvent(
    @RequestParam("eventId") Long eventId
  ) {
    eventService.clone(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/bundle/clone")
  @Transactional
  public void cloneBundle (
    @RequestParam("eventId") Long eventId,
    @RequestParam("typeId") Long bundleId
  ) {
    Bundle newBundle = bundleService.clone(eventService.findByEventId(eventId), bundleService.findByBundleId(bundleId), null);
    EventBundle newEventBundle = new EventBundle(
      newBundle,
      eventService.findByEventId(eventId)
    );
    eventService.save(newEventBundle);
  }

  @GetMapping(path = "/track/clone")
  @Transactional
  public void cloneTrack (
    @RequestParam("eventId") Long eventId,
    @RequestParam("typeId") Long trackId
  ) {
    trackService.clone(eventService.findByEventId(eventId), trackService.findByTrackId(trackId));
  }

  @PostMapping(path = "/requireTerms/upsert")
  @Transactional
  public void upsertTerms(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonTermsObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.updateTerms(
        jsonTermsObject,
        eventService.findByEventId(jsonTermsObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/requireTerms/delete")
  @Transactional
  public void deleteTerms(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonTermsObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      eventService.deleteTerms(
        jsonTermsObject,
        eventService.findByEventId(jsonTermsObject.get("id").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

}
