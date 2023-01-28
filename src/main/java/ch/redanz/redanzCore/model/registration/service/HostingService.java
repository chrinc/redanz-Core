package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.*;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SleepUtilService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Host;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
@Slf4j
public class HostingService {
  private final HosteeRegistrationRepo hosteeRegistrationRepo;
  private final HostRegistrationRepo hostRegistrationRepo;
  private final HosteeSleepUtilsRegistrationRepo hosteeSleepUtilsRegistrationRepo;
  private final HostSleepUtilRegistrationRepo hostSleepUtilRegistrationRepo;
  private final HosteeSlotRegistrationRepo hosteeSlotRegistrationRepo;
  private final HostSlotRegistraitionRepo hostSlotRegistrationRepo;
  private final SleepUtilService sleepUtilService;
  private final SlotService slotService;
  private final EventService eventService;

  private final OutTextService outTextService;

  public String getSlots(HostRegistration hostRegistration, Language language) {
    AtomicReference<String> slots = new AtomicReference<>();
    hostSlotRegistrationRepo.findAllByHostRegistration(hostRegistration).forEach(slot ->{
      String slotOutText = outTextService.getOutTextByKeyAndLangKey(slot.getSlot().getName(), language.getLanguageKey()).getOutText();
      if (slots.get() == null) {
        slots.set(slotOutText);
      } else {
        slots.set(slots.get() + ", " + slotOutText);
      }
    });
    return slots.get() == null ? "" : slots.toString();
  }
  public String getSlots(HosteeRegistration hosteeRegistration, Language language) {
    AtomicReference<String> slots = new AtomicReference<>();
    hosteeSlotRegistrationRepo.findAllByHosteeRegistration(hosteeRegistration).forEach(slot ->{
      String slotOutText = outTextService.getOutTextByKeyAndLangKey(slot.getSlot().getName(), language.getLanguageKey()).getOutText();
      if (slots.get() == null) {
        slots.set(slotOutText);
      } else {
        slots.set(slots.get() + ", " + slotOutText);
      }
    });
    return slots.get() == null ? "" : slots.toString();
  }

  public JsonArray hostRegistrationArray(JsonObject hostRegistrationRequest) {
    log.info("hostRegistrationArray ");
    JsonElement hostRegistration = hostRegistrationRequest.get("hostRegistration");
    log.info("hostRegistration: " + hostRegistration);
    log.info("jsonArrey: " + hostRegistration.getAsJsonArray());

    if (hostRegistration != null
      && !hostRegistration.getAsJsonArray().isEmpty()) {
      return hostRegistration
        .getAsJsonArray();
    }
    return null;
  }

  public void updateHostRegistrationRequest(Registration registration, JsonObject request) {
    JsonArray hostRegistrationArray = hostRegistrationArray(request);
    log.info("hostRegistrationArray: " + hostRegistrationArray);
    if (hostRegistrationArray != null) {

      // update existing
      updateHostRegistration(registration, hostRegistrationArray);
      updateHostSleepUtilRegistration(registration, hostRegistrationArray);
      updateHostSlotRegistration(registration, hostRegistrationArray);

    } else {

      // delete existing host registration
      if (hostRegistrationRepo.findAllByRegistration(registration) != null) {
        hostSlotRegistrationRepo.deleteAllByHostRegistration(hostRegistrationRepo.findAllByRegistration(registration));
        hostSleepUtilRegistrationRepo.deleteAllByHostRegistration(hostRegistrationRepo.findAllByRegistration(registration));
        hostRegistrationRepo.deleteAllByRegistration(registration);
      }
    }
  }

  public String getUtils(HostRegistration hostRegistration, Language language) {
    AtomicReference<String> sleepUtils = new AtomicReference<>();
    hostSleepUtilRegistrationRepo.findAllByHostRegistration(hostRegistration).forEach(sleepUtilRegistration ->{
      String sleepUtilOutText =
        outTextService.getOutTextByKeyAndLangKey(sleepUtilRegistration.getSleepUtil().getName(), language.getLanguageKey()).getOutText()
          + ": " + sleepUtilRegistration.getSleepUtilCount()
        ;
      if (sleepUtils.get() == null) {
        sleepUtils.set(sleepUtilOutText) ;
      } else {
        sleepUtils.set(sleepUtils.get() + ", " + sleepUtilOutText);
      }
    });
    return sleepUtils.get() == null ? "" : sleepUtils.toString();
  }

  public String getUtils(HosteeRegistration hosteeRegistration, Language language) {
    AtomicReference<String> sleepUtils = new AtomicReference<>();
    hosteeSleepUtilsRegistrationRepo.findAllByHosteeRegistration(hosteeRegistration).forEach(sleepUtilRegistration ->{
      String sleepUtilOutText = outTextService.getOutTextByKeyAndLangKey(sleepUtilRegistration.getSleepUtil().getName(), language.getLanguageKey()).getOutText();
      if (sleepUtils.get() == null) {
        sleepUtils.set(sleepUtilOutText);
      } else {
        sleepUtils.set(sleepUtils.get() + ", " + sleepUtilOutText);
      }
    });
    return sleepUtils.get() == null ? "" : sleepUtils.toString();
  }

  public void updateHostRegistration(Registration registration, JsonArray hostRegistration) {
    JsonObject personCountJson = hostRegistration.get(1).getAsJsonObject();
    JsonObject hostCommentJson = hostRegistration.get(3).getAsJsonObject();

    HostRegistration existingHostregistration = hostRegistrationRepo.findAllByRegistration(registration);

    int requestPersonCount = personCountJson.get("personCount").isJsonNull() ? 0 : personCountJson.get("personCount").getAsInt();
    String hostComment = hostCommentJson.get("hostComment") == null ? "" : hostCommentJson.get("hostComment").getAsString();

    if (existingHostregistration != null) {

      // update existing registration
      if (existingHostregistration.getHostedPersonCount() != requestPersonCount) {
        existingHostregistration.setHostedPersonCount(requestPersonCount);
      }
      if (existingHostregistration.getHostComment() != hostComment) {
        existingHostregistration.setHostComment(hostComment);
      }
      hostRegistrationRepo.save(existingHostregistration);
    } else {

      // new host registration
      hostRegistrationRepo.save(
        new HostRegistration(
          registration,
          requestPersonCount,
          hostComment
        )
      );
    }
  }

  private boolean hasHostSleepUtilRegistration(List<HostSleepUtilRegistration> hostSleepUtilRegistrations, SleepUtil sleepUtil) {
    AtomicBoolean hasHostSleepUtilRegistration = new AtomicBoolean(false);
    hostSleepUtilRegistrations.forEach(hostSleepUtilRegistration -> {
      if (hostSleepUtilRegistration.getSleepUtil() == sleepUtil) {
        hasHostSleepUtilRegistration.set(true);
      }
    });

    return hasHostSleepUtilRegistration.get();
  }

  public void updateHostSleepUtilRegistration(Registration registration, JsonArray hostRegistration) {
    List<HostSleepUtilRegistration> requestHostSleepUtilRegistrations = hostSleepUtilRegistrations(registration, hostRegistration);
    HostRegistration existingHostRegistration = hostRegistrationRepo.findAllByRegistration(registration);
    List<HostSleepUtilRegistration> hostSleepUtilRegistrations = hostSleepUtilRegistrationRepo.findAllByHostRegistration(existingHostRegistration);

    // delete in current if not in request
    hostSleepUtilRegistrations.forEach(hostSleepUtilRegistration -> {
      if (
        !hasHostSleepUtilRegistration(requestHostSleepUtilRegistrations, hostSleepUtilRegistration.getSleepUtil())
      ){
        hostSleepUtilRegistrationRepo.deleteAllByHostRegistrationAndSleepUtil(existingHostRegistration, hostSleepUtilRegistration.getSleepUtil());
      }
    });

    // add new from request
    requestHostSleepUtilRegistrations.forEach(hostSleepUtilRegistration -> {
      if (!hasHostSleepUtilRegistration(hostSleepUtilRegistrations, hostSleepUtilRegistration.getSleepUtil())){
        hostSleepUtilRegistrationRepo.save(hostSleepUtilRegistration);
      } else {
        HostSleepUtilRegistration existingSleepUtilRegistration = hostSleepUtilRegistrationRepo.findAllByHostRegistrationAndSleepUtil(
          existingHostRegistration, hostSleepUtilRegistration.getSleepUtil());
        if (existingSleepUtilRegistration.getSleepUtilCount() != hostSleepUtilRegistration.getSleepUtilCount()) {
          existingSleepUtilRegistration.setSleepUtilCount(hostSleepUtilRegistration.getSleepUtilCount());
        }
      }
    });
  }


  private boolean hasHostSlotRegistration(List<HostSlotRegistration> hostSlotRegistrations, Slot slot) {
    AtomicBoolean hasHostSlotRegistration = new AtomicBoolean(false);
    hostSlotRegistrations.forEach(hostSlotRegistration -> {
      if (hostSlotRegistration.getSlot() == slot) {
        hasHostSlotRegistration.set(true);
      }
    });

    return hasHostSlotRegistration.get();
  }

  public void updateHostSlotRegistration(Registration registration, JsonArray hostRegistration) {
    List<HostSlotRegistration> requestHostSlotRegistrations = hostSlotRegistrations(registration, hostRegistration);
    HostRegistration existingHostRegistration = hostRegistrationRepo.findAllByRegistration(registration);
    List<HostSlotRegistration> hostSlotRegistrations = hostSlotRegistrationRepo.findAllByHostRegistration(existingHostRegistration);

    // delete in current if not in request
    hostSlotRegistrations.forEach(hostSlotRegistration -> {
      if (!hasHostSlotRegistration(requestHostSlotRegistrations, hostSlotRegistration.getSlot())){
        hostSlotRegistrationRepo.deleteAllByHostRegistrationAndSlot(existingHostRegistration, hostSlotRegistration.getSlot());
      }
    });

    // add new from request
    requestHostSlotRegistrations.forEach(hostSlotRegistration -> {
      if (!hasHostSlotRegistration(hostSlotRegistrations, hostSlotRegistration.getSlot())){
        hostSlotRegistrationRepo.save(hostSlotRegistration);
      }
    });
  }

  public List<HostSleepUtilRegistration> hostSleepUtilRegistrations (Registration registration, JsonArray hostRegistration) {
    List<HostSleepUtilRegistration> hostSleepUtilRegistrations = new ArrayList<>();
    JsonObject sleepUtilsJson = hostRegistration.get(2).getAsJsonObject();

    // host sleep util registration
    if (sleepUtilsJson.get("sleepUtils") != null) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {
        if (sleepUtil.getAsJsonObject().get("count") != null
          && !sleepUtil.getAsJsonObject().get("count").isJsonNull()
        ) {
          hostSleepUtilRegistrations.add(
            new HostSleepUtilRegistration(
              hostRegistrationRepo.findAllByRegistration(registration),
              sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong()),
              sleepUtil.getAsJsonObject().get("count").getAsInt()
            )
          );
        }
        ;
      });
    }
    return hostSleepUtilRegistrations;
  }

  public List<HostSlotRegistration> hostSlotRegistrations (Registration registration, JsonArray hostRegistration) {
    List<HostSlotRegistration> hostSlotRegistrations = new ArrayList<>();
    JsonObject slotsJson = hostRegistration.get(0).getAsJsonObject();

    // host slot registration
    if (slotsJson.get("slots") != null) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
        hostSlotRegistrations.add(
          new HostSlotRegistration(
            hostRegistrationRepo.findAllByRegistration(registration),
            slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
    return hostSlotRegistrations;
  }

  public List<HostRegistration> getAllHostRegistrationsByEvent(Event event){
    return hostRegistrationRepo.findAllByRegistrationEvent(event);
  }
  public List<HosteeRegistration> getAllHosteeRegistrationsByEvent(Event event){
    return hosteeRegistrationRepo.findAllByRegistrationEvent(event);
  }

  public Map<String, List<Object>> getHosteeRegistration(Registration registration) {
    Map<String, List<Object>> hosteeRegistrationMap = new HashMap<>();

    HosteeRegistration hosteeRegistration = hosteeRegistrationRepo.findByRegistration(registration);
    List<Object> hosteeRegistrations = new ArrayList<>();
    hosteeRegistrations.add(hosteeRegistration);
    hosteeRegistrationMap.put(
      "hosteeRegistration"
      , hosteeRegistrations
    );

    hosteeRegistrationMap.put(
      "hosteeSleepUtilRegistration"
      , Collections.singletonList(hosteeSleepUtilsRegistrationRepo.findAllByHosteeRegistration(hosteeRegistration))
    );

    hosteeRegistrationMap.put(
      "hosteeSlotRegistration"
      , Collections.singletonList(hosteeSlotRegistrationRepo.findAllByHosteeRegistration(hosteeRegistration))
    );

    return hosteeRegistrationMap;
  }

  public Map<String, List<Object>> getHostRegistration(Registration registration) {
    Map<String, List<Object>> hostRegistrationMap = new HashMap<>();

    HostRegistration hostRegistration = hostRegistrationRepo.findAllByRegistration(registration);
    List<Object> hostRegistrations = new ArrayList<>();
    hostRegistrations.add(hostRegistration);
    hostRegistrationMap.put(
      "hostRegistration"
      , hostRegistrations
    );

    hostRegistrationMap.put(
      "hostSleepUtilRegistration"
      , Collections.singletonList(hostSleepUtilRegistrationRepo.findAllByHostRegistration(hostRegistration))
    );

    hostRegistrationMap.put(
      "hostSlotRegistration"
      , Collections.singletonList(hostSlotRegistrationRepo.findAllByHostRegistration(hostRegistration))
    );

    return hostRegistrationMap;
  }

  // Hostee Registration
  private boolean hasHosteeSlotRegistration(List<HosteeSlotRegistration> hosteeSlotRegistrations, Slot slot) {
    AtomicBoolean hasHosteeSlotRegistration = new AtomicBoolean(false);
    hosteeSlotRegistrations.forEach(hosteeSlotRegistration -> {
      if (hosteeSlotRegistration.getSlot() == slot) {
        hasHosteeSlotRegistration.set(true);
      }
    });

    return hasHosteeSlotRegistration.get();
  }

  public List<HosteeSlotRegistration> hosteeSlotRegistrations (Registration registration, JsonArray hosteeRegistration) {
    List<HosteeSlotRegistration> hosteeSlotRegistrations = new ArrayList<>();
    JsonObject slotsJson = hosteeRegistration.get(0).getAsJsonObject();

    // host slot registration
    if (slotsJson.get("slots") != null) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
        hosteeSlotRegistrations.add(
          new HosteeSlotRegistration(
            hosteeRegistrationRepo.findByRegistration(registration),
            slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
    return hosteeSlotRegistrations;
  }

  public void updateHosteeSlotRegistration(Registration registration, JsonArray hosteeRegistration) {
    List<HosteeSlotRegistration> requestHosteeSlotRegistrations = hosteeSlotRegistrations(registration, hosteeRegistration);
    HosteeRegistration existingHosteeRegistration = hosteeRegistrationRepo.findByRegistration(registration);
    List<HosteeSlotRegistration> hosteeSlotRegistrations = hosteeSlotRegistrationRepo.findAllByHosteeRegistration(existingHosteeRegistration);

    // delete in current if not in request
    hosteeSlotRegistrations.forEach(hosteeSlotRegistration -> {
      if (!hasHosteeSlotRegistration(requestHosteeSlotRegistrations, hosteeSlotRegistration.getSlot())){
        hosteeSlotRegistrationRepo.deleteAllByHosteeRegistrationAndSlot(existingHosteeRegistration, hosteeSlotRegistration.getSlot());
      }
    });

    // add new from request
    requestHosteeSlotRegistrations.forEach(hosteeSlotRegistration -> {
      if (!hasHosteeSlotRegistration(hosteeSlotRegistrations, hosteeSlotRegistration.getSlot())){
        hosteeSlotRegistrationRepo.save(hosteeSlotRegistration);
      }
    });
  }

  public List<HosteeSleepUtilRegistration> hosteeSleepUtilRegistrations (Registration registration, JsonArray hosteeRegistration) {
    List<HosteeSleepUtilRegistration> hosteeSleepUtilRegistrations = new ArrayList<>();
    JsonObject sleepUtilsJson = hosteeRegistration.get(4).getAsJsonObject();
    log.info("sleepUtilsJson: " + sleepUtilsJson);
    // host sleep util registration
    if (sleepUtilsJson.get("sleepUtils") != null) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {
        if (sleepUtil.getAsJsonObject().get("checked") != null
          && sleepUtil.getAsJsonObject().get("checked").getAsBoolean()
        ) {
          hosteeSleepUtilRegistrations.add(
            new HosteeSleepUtilRegistration(
              hosteeRegistrationRepo.findByRegistration(registration),
              sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong())
            )
          );
        }
      });
    }
    return hosteeSleepUtilRegistrations;
  }

  private boolean hasHosteeSleepUtilRegistration(List<HosteeSleepUtilRegistration> hosteeSleepUtilRegistrations, SleepUtil sleepUtil) {
    AtomicBoolean hasHosteeSleepUtilRegistration = new AtomicBoolean(false);
    hosteeSleepUtilRegistrations.forEach(hosteeSleepUtilRegistration -> {
      if (hosteeSleepUtilRegistration.getSleepUtil() == sleepUtil) {
        hasHosteeSleepUtilRegistration.set(true);
      }
    });

    return hasHosteeSleepUtilRegistration.get();
  }

  public void updateHosteeSleepUtilRegistration(Registration registration, JsonArray hosteeRegistration) {
    List<HosteeSleepUtilRegistration> requestHosteeSleepUtilRegistrations = hosteeSleepUtilRegistrations(registration, hosteeRegistration);
    HosteeRegistration existingHosteeRegistration = hosteeRegistrationRepo.findByRegistration(registration);
    List<HosteeSleepUtilRegistration> hosteeSleepUtilRegistrations = hosteeSleepUtilsRegistrationRepo.findAllByHosteeRegistration(existingHosteeRegistration);
    log.info("hosteeSleepUtilRegistrations: " + hosteeSleepUtilRegistrations);
    log.info("requestHosteeSleepUtilRegistrations: " + requestHosteeSleepUtilRegistrations);
    log.info("hosteeSleepUtilRegistrations size: " + hosteeSleepUtilRegistrations.size());
    log.info("requestHosteeSleepUtilRegistrations size: " + requestHosteeSleepUtilRegistrations.size());
    // delete in current if not in request
    hosteeSleepUtilRegistrations.forEach(hosteeSleepUtilRegistration -> {
      if (
        !hasHosteeSleepUtilRegistration(requestHosteeSleepUtilRegistrations, hosteeSleepUtilRegistration.getSleepUtil())
      ){
        hosteeSleepUtilsRegistrationRepo.deleteAllByHosteeRegistrationAndSleepUtil(existingHosteeRegistration, hosteeSleepUtilRegistration.getSleepUtil());
      }
    });

    // add new from request
    requestHosteeSleepUtilRegistrations.forEach(hosteeSleepUtilRegistration -> {
      if (!hasHosteeSleepUtilRegistration(hosteeSleepUtilRegistrations, hosteeSleepUtilRegistration.getSleepUtil())){
        hosteeSleepUtilsRegistrationRepo.save(hosteeSleepUtilRegistration);
      }
    });
  }

  public void updateHosteeRegistration(Registration registration, JsonArray hosteeRegistration) {
    JsonObject isShareRoomsJson = hosteeRegistration.get(1).getAsJsonObject();
    JsonObject nameRoomMateJson = hosteeRegistration.get(2).getAsJsonObject();
    JsonObject isSharedBedJson = hosteeRegistration.get(3).getAsJsonObject();
    JsonObject hosteeCommentJson = hosteeRegistration.get(5).getAsJsonObject();

    HosteeRegistration existingHosteeRegistration = hosteeRegistrationRepo.findByRegistration(registration);

    log.info("get attributes");
    boolean isShareRooms = !isShareRoomsJson.get("isShareRooms").isJsonNull() && isShareRoomsJson.get("isShareRooms").getAsBoolean();
    log.info("isShareRooms: " + isShareRooms);
    String nameRoomMate =
      nameRoomMateJson.get("nameRoomMate") == null  || nameRoomMateJson.get("nameRoomMate").isJsonNull() ?
        null : nameRoomMateJson.get("nameRoomMate").getAsString();
    log.info("nameRoomMate: " + nameRoomMate);
    boolean isSharedBed =  isSharedBedJson.get("isSharedBed") != null && !isSharedBedJson.get("isSharedBed").isJsonNull() && isSharedBedJson.get("isSharedBed").getAsBoolean();
    log.info("isSharedBed: " + isSharedBed);
    String hosteeComment = hosteeCommentJson.get("hosteeComment") == null
      || hosteeCommentJson.get("hosteeComment").isJsonNull() ?
      null : hosteeCommentJson.get("hosteeComment").getAsString();
    log.info("hosteeComment: " + hosteeComment);
    if (existingHosteeRegistration != null) {

      // update existing registration
      if (existingHosteeRegistration.getNameRoomMate() != nameRoomMate) {
        existingHosteeRegistration.setNameRoomMate(nameRoomMate);
      }
      if (existingHosteeRegistration.getComment() != hosteeComment) {
        existingHosteeRegistration.setComment(hosteeComment);
      }
      if (existingHosteeRegistration.isSharedRooms() ^ isShareRooms) {
        existingHosteeRegistration.setSharedRooms(isShareRooms);
      }
      if (existingHosteeRegistration.isSharedBed() ^ isSharedBed) {
        existingHosteeRegistration.setSharedBed(isSharedBed);
      }
      if (!existingHosteeRegistration.isSharedRooms()) {
        existingHosteeRegistration.setNameRoomMate(null);
        existingHosteeRegistration.setSharedBed(false);
      }
      hosteeRegistrationRepo.save(existingHosteeRegistration);
    } else {

      // new host registration
      hosteeRegistrationRepo.save(
        new HosteeRegistration(
          registration,
          isShareRooms,
          nameRoomMate,
          isSharedBed,
          hosteeComment
        )
      );
    }
  }

  public JsonArray hosteeRegistrationArray(JsonObject hosteeRegistrationRequest) {
    log.info("hosteeRegistrationRequest: " + hosteeRegistrationRequest);
    if (hosteeRegistrationRequest.get("hosteeRegistration") != null
      && !hosteeRegistrationRequest.get("hosteeRegistration").getAsJsonArray().isEmpty()) {
      return hosteeRegistrationRequest
        .get("hosteeRegistration")
        .getAsJsonArray();
    }
    return null;
  }

  public void updateHosteeRegistrationRequest(Registration registration, JsonObject request) {
    JsonArray hosteeRegistrationArray = hosteeRegistrationArray(request);
    log.info("hosteeRegistrationArray: " + hosteeRegistrationArray);

    if (hosteeRegistrationArray != null) {

      // update existing
      updateHosteeRegistration(registration, hosteeRegistrationArray);
      log.info("updateHosteeRegistration done");
      updateHosteeSleepUtilRegistration(registration, hosteeRegistrationArray);
      log.info("updateHosteeSleepUtilRegistration done");
      updateHosteeSlotRegistration(registration, hosteeRegistrationArray);
      log.info("updateHosteeSlotRegistration done");

    } else {

      // delete existing host registration
      if (hosteeRegistrationRepo.findByRegistration(registration) != null) {
        hosteeSlotRegistrationRepo.deleteAllByHosteeRegistration(hosteeRegistrationRepo.findByRegistration(registration));
        hosteeSleepUtilsRegistrationRepo.deleteAllByHosteeRegistration(hosteeRegistrationRepo.findByRegistration(registration));
        hosteeRegistrationRepo.deleteAllByRegistration(registration);
      }
    }
  }
}
