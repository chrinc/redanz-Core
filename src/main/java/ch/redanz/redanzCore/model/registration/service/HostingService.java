package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.*;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.workshop.service.SleepUtilService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class HostingService {
  private final HosteeRegistrationRepo hosteeRegistrationRepo;
  private final HostRegistrationRepo hostRegistrationRepo;
  private final HosteeSleepUtilsRegistrationRepo hosteeSleepUtilsRegistrationRepo;
  private final HostSleepUtilRegistrationRepo hostSleepUtilRegistrationRepo;

  private final HosteeSlotRegistrationRepo hosteeSlotRegistrationRepo;
  private final HostSlotRegistraitionRepo hostSlotRegistraitionRepo;
  private final SleepUtilService sleepUtilService;
  private final SlotService slotService;

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
        nameRoomMateJson.get("nameRoomMate").isJsonNull() ? null : nameRoomMateJson.get("nameRoomMate").getAsString(),
        !isSharedBedJson.get("isSharedBed").isJsonNull() && isSharedBedJson.get("isSharedBed").getAsBoolean(),
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
        }
        ;
      });
    }

    // host slot registration
    if (slotsJson.get("slots") != null) {
      slotsJson.get("slots").getAsJsonArray().forEach(slot -> {
        hostSlotRegistraitionRepo.save(
          new HostSlotRegistration(
            hostRegistrationRepo.findAllByRegistration(registration),
            slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
  }

  public Map<String, List<Object>> getHosteeRegistrations(Registration registration) {
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

  public Map<String, List<Object>> getHostRegistrations(Registration registration) {
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
      , Collections.singletonList(hostSlotRegistraitionRepo.findAllByHostRegistration(hostRegistration))
    );

    return hostRegistrationMap;
  }
}
