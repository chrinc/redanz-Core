package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.*;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SleepUtilService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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

  public void saveHosteeRegistration(Registration registration, JsonArray hosteeRegistration) {
    JsonObject slotsJson = hosteeRegistration.get(0).getAsJsonObject();
    JsonObject isShareRoomsJson = hosteeRegistration.get(1).getAsJsonObject();
    JsonObject nameRoomMateJson = hosteeRegistration.get(2).getAsJsonObject();
    JsonObject isSharedBedJson = hosteeRegistration.get(3).getAsJsonObject();
    JsonObject sleepUtilsJson = hosteeRegistration.get(4).getAsJsonObject();
    JsonObject hosteeCommentJson = hosteeRegistration.get(5).getAsJsonObject();
    // host registration
    hosteeRegistrationRepo.save(
      new HosteeRegistration(
        registration,
        !isShareRoomsJson.get("isShareRooms").isJsonNull() && isShareRoomsJson.get("isShareRooms").getAsBoolean(),
        nameRoomMateJson.get("nameRoomMate") == null ? null : nameRoomMateJson.get("nameRoomMate").getAsString(),
        isSharedBedJson.get("isSharedBed") != null && !isSharedBedJson.get("isSharedBed").isJsonNull() && isSharedBedJson.get("isSharedBed").getAsBoolean(),
        hosteeCommentJson.get("hosteeComment") == null ? null : hosteeCommentJson.get("hosteeComment").getAsString()
      )
    );
    // hostee sleep util registration
    if (!sleepUtilsJson.get("sleepUtils").isJsonNull()) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {

        if (sleepUtil.getAsJsonObject().get("checked") != null && sleepUtil.getAsJsonObject().get("checked").getAsBoolean()) {
          hosteeSleepUtilsRegistrationRepo.save(
            new HosteeSleepUtilRegistration(
              hosteeRegistrationRepo.findByRegistration(registration),
              sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong())
            )
          );
        }
      });
    }

    // hostee slot registration
    if (!slotsJson.get("slots").isJsonNull()) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
        hosteeSlotRegistrationRepo.save(
          new HosteeSlotRegistration(
            hosteeRegistrationRepo.findByRegistration(registration),
            slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
  }

  public void saveHostRegistration(Registration registration, JsonArray hostRegistration) {
    JsonObject slotsJson = hostRegistration.get(0).getAsJsonObject();
    JsonObject personCountJson = hostRegistration.get(1).getAsJsonObject();
    JsonObject sleepUtilsJson = hostRegistration.get(2).getAsJsonObject();
    JsonObject hostCommentJson = hostRegistration.get(3).getAsJsonObject();
    // host registration
    hostRegistrationRepo.save(
      new HostRegistration(
        registration,
        personCountJson.get("personCount").isJsonNull() ? 0 : personCountJson.get("personCount").getAsInt(),
        hostCommentJson.get("hostComment") == null ? "" : hostCommentJson.get("hostComment").getAsString()
      )
    );

    // host sleep util registration
    if (sleepUtilsJson.get("sleepUtils") != null) {
      sleepUtilsJson.get("sleepUtils").getAsJsonArray().forEach(sleepUtil -> {
        if (sleepUtil.getAsJsonObject().get("count") != null) {
          hostSleepUtilRegistrationRepo.save(
            new HostSleepUtilRegistration(
              hostRegistrationRepo.findAllByRegistration(registration),
              sleepUtilService.findBySleepUtilId(sleepUtil.getAsJsonObject().get("sleepUtilId").getAsLong()),
              sleepUtil.getAsJsonObject().get("count").getAsInt()
            )
          );
        };
      });
    }

    // host slot registration
    if (slotsJson.get("slots") != null) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
        hostSlotRegistrationRepo.save(
          new HostSlotRegistration(
            hostRegistrationRepo.findAllByRegistration(registration),
            slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
  }
  public List<HostRegistration> getAllHostRegistrations(){
    return hostRegistrationRepo.findAll();
  }

  public List<HosteeRegistration> getAllHosteeRegistrations(){
    return hosteeRegistrationRepo.findAll();
  }

  public List<HostRegistration> getAllHostRegistrationsCurrentEvent(){

    return hostRegistrationRepo.findAllByRegistrationEvent(eventService.getCurrentEvent());
  }

  public List<HosteeRegistration> getAllHosteeRegistrationsCurrentEvent(){

    return hosteeRegistrationRepo.findAllByRegistrationEvent(eventService.getCurrentEvent());
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
}
