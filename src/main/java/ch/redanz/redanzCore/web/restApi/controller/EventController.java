package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @GetMapping(path = "/schema/event")
  public List<Map<String, String>> getEventSchema() {
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
  public List<Map<String, String>> getBundleSchema() {
    return eventService.getBundleSchema();
  }

  @GetMapping(path = "/data/bundle")
  public List<Map<String, String>> getBundleData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getBundlesData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/data/bundle/all")
  public List<Map<String, String>> getAllBundleData() {
    return eventService.getBundlesData(eventService.getAllEvents());
  }

  @GetMapping(path = "/schema/track")
  public List<Map<String, String>> getTrackSchema(
    @RequestParam("eventId") Long eventId) {
    return trackService.getTrackSchema(eventService.findByEventId(eventId));
  }

  @GetMapping(path = "/data/track")
  public List<Map<String, String>> getTrackData(
    @RequestParam("bundleId") Long bundleId
  ) {
    return trackService.getTracksData(new ArrayList<>(){{ add(bundleService.findByBundleId(bundleId)); }});
  }

  @GetMapping(path = "/schema/discount")
  public List<Map<String, String>> getDiscountSchema() {
    return discountService.getDiscountSchema();
  }

  @GetMapping(path = "/data/discount")
  public List<Map<String, String>> getDiscountData(
    @RequestParam("eventId") Long eventId
  ) {
    return discountService.getDiscountData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/schema/special")
  public List<Map<String, String>> getSpecialSchema() {
    return specialService.getSpecialSchema();
  }

  @GetMapping(path = "/data/special")
  public List<Map<String, String>> getSpecialData(
    @RequestParam("eventId") Long eventId
  ) {
    return specialService.getSpecialData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/schema/private")
  public List<Map<String, String>> getPrivateSchema() {
    return privateClassService.getPrivateSchema();
  }

  @GetMapping(path = "/data/private")
  public List<Map<String, String>> getPrivateData(
    @RequestParam("eventId") Long eventId
  ) {
    return privateClassService.getPrivateData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/schema/food")
  public List<Map<String, String>> getFoodSchema() {
    return foodService.getFoodSchema();
  }

  @GetMapping(path = "/data/food")
  public List<Map<String, String>> getFoodData(
    @RequestParam("eventId") Long eventId
  ) {
    return foodService.getFoodData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
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
    return eventService.getAllEvents();
  }

  @GetMapping(path = "/byId")
  @Transactional
  public Event getUserActiveRegistration(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      return eventService.getById(eventId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/current")
  public Event getCurrentEvent() {
    return eventService.getCurrentEvent();
  }

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

  @GetMapping(path = "/special/all")
  public Set<Special> getSpecials(
    @RequestParam("bundleId") Long bundleId
  ) {
    // log.info("inc@special/all, bundleId: " + bundleId);
    if (bundleId == null) {
      return specialService.findByEventOrBundle(eventService.getCurrentEvent());
    } else {
      return specialService.findByBundle(bundleService.findByBundleId(bundleId));
    }
  }
  @GetMapping(path = "/privateClasses/all")
  public List<PrivateClass> getPrivateClasses(
    @RequestParam("bundleId") Long bundleId,
    @RequestParam("eventId") Long eventId
  ) {
    List<PrivateClass> privateClasses;
    if (bundleId == null) {
      privateClasses =  privateClassService.findByEvent(eventService.findByEventId(eventId));
    } else {
      privateClasses =  privateClassService.findByEventAndBundle(eventService.findByEventId(eventId), bundleService.findByBundleId(bundleId));
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
      eventService.deleteEvent(
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );

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
      bundleService.deleteBundle(
        bundleObject
      );

      eventService.deleteEventBundle(
        bundleObject
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
        bundleService.findByBundleId(jsonTrackObject.get("bundleId").getAsLong())
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
      // update
      trackService.deleteTrack(
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );

      bundleService.deleteBundleTrack(
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/food/upsert")
  @Transactional
  public void upsertFood(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonFoodObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      foodService.updateFood(
        jsonFoodObject,
        eventService.findByEventId(jsonFoodObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/food/delete")
  @Transactional
  public void deleteFood(
    @RequestBody String jsonObject
  ) {
    try {
      // update
      JsonObject jsonFoodObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      foodService.deleteFood(
        jsonFoodObject,
        eventService.findByEventId(jsonFoodObject.get("eventId").getAsLong())
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

      JsonObject jsonDiscountObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      discountService.updateDiscount(
        jsonDiscountObject,
        eventService.findByEventId(jsonDiscountObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/discount/delete")
  @Transactional
  public void deleteDiscount(
    @RequestBody String jsonObject
  ) {
    try {
      // update
      JsonObject jsonDiscountObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      discountService.deleteDiscount(
        jsonDiscountObject,
        eventService.findByEventId(jsonDiscountObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/special/upsert")
  @Transactional
  public void upsertSpecial(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonSpecialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      specialService.updateSpecial(
        jsonSpecialObject,
        eventService.findByEventId(jsonSpecialObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/special/delete")
  @Transactional
  public void deleteSpecial(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonSpecialObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      specialService.deleteSpecial(
        jsonSpecialObject,
        eventService.findByEventId(jsonSpecialObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/private/upsert")
  @Transactional
  public void upsertPrivate(
    @RequestBody String jsonObject
  ) {
    try {

      JsonObject jsonPrivateObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      privateClassService.updatePrivates(
        jsonPrivateObject,
        eventService.findByEventId(jsonPrivateObject.get("eventId").getAsLong())
      );

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/private/delete")
  @Transactional
  public void deletePrivate(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject jsonPrivateObject = JsonParser.parseString(jsonObject).getAsJsonObject();
      privateClassService.deletePrivates(
        jsonPrivateObject,
        eventService.findByEventId(jsonPrivateObject.get("eventId").getAsLong())
      );

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
}
