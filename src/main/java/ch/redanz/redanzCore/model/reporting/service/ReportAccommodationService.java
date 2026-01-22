package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.HostingService;
import ch.redanz.redanzCore.model.reporting.response.ResponseAccommodation;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.AccommodationService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ReportAccommodationService {
  private final AccommodationService accommodationService;
  private final OutTextService outTextService;
  private final HostingService hostingService;

  public List<ResponseAccommodation> getAccommodationReport(Language language, Event event) {
    List<ResponseAccommodation> accommodations = new ArrayList<>();
    hostingService.getAllHostRegistrationsByEvent(event).forEach(hostRegistration -> {
      Registration registration = hostRegistration.getRegistration();
      accommodations.add(
        new ResponseAccommodation(
          registration.getParticipant().getPersonId()
          , registration.getRegistrationId()
          , outTextService.getOutTextMapByKey(registration.getWorkflowStatus().getLabel()).toString()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , outTextService.getOutTextMapByKey("LABEL-HOST").toString()
          , hostRegistration.getHostedPersonCount()
          , hostingService.getSlots(hostRegistration)
          , ""
          , hostingService.getUtils(hostRegistration)
          , registration.getParticipant().getStreet()
          , registration.getParticipant().getCity()
          , hostRegistration.getHostComment()
          , registration.getRegistrationType().name()
        )
      );
    });
    hostingService.getAllHosteeRegistrationsByEvent(event).forEach(hosteeRegistration -> {
      Registration registration = hosteeRegistration.getRegistration();
      accommodations.add(
        new ResponseAccommodation(
          registration.getParticipant().getPersonId()
          , registration.getRegistrationId()
          , outTextService.getOutTextMapByKey(registration.getWorkflowStatus().getLabel()).toString()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , outTextService.getOutTextMapByKey("LABEL-HOSTEE").toString()
          , null
          , hostingService.getSlots(hosteeRegistration)
          , roomMate(hosteeRegistration)
          , hostingService.getUtils(hosteeRegistration)
          , registration.getParticipant().getStreet()
          , registration.getParticipant().getCity()
          , hosteeRegistration.getComment()
          , registration.getRegistrationType().name()
        )
      );
    });
    return accommodations;
  }

  private String roomMate(HosteeRegistration hosteeRegistration) {
    if (hosteeRegistration.getNameRoomMate() == null) return "";
    List<Map<String, String>> roomMates = outTextService.getOutTextMapByKey("LABEL-HOSTEE-SHARED-BED");
    roomMates.get(0).keySet().forEach(roomMateKey -> {
      roomMates.get(0).put(roomMateKey, hosteeRegistration.getNameRoomMate()
        + (hosteeRegistration.isSharedBed() ?
        " (" + roomMates.get(0).get(roomMateKey) + ")"
        : "")
      );
    });
    return roomMates.isEmpty() ? "" : roomMates.toString();
  }
}

