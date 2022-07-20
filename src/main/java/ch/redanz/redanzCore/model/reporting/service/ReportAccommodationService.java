package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.HostingService;
import ch.redanz.redanzCore.model.reporting.response.ResponseAccommodation;
import ch.redanz.redanzCore.model.reporting.response.ResponseVolunteer;
import ch.redanz.redanzCore.model.workshop.service.AccommodationService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportAccommodationService {
  private final AccommodationService accommodationService;
  private final OutTextService outTextService;
  private final HostingService hostingService;
  public List<ResponseAccommodation> getAccommodationReport(Language language) {
    List<ResponseAccommodation> accommodations = new ArrayList<>();
    hostingService.getAllHostRegistrations().forEach(hostRegistration -> {
      Registration registration = hostRegistration.getRegistration();
      accommodations.add(
        new ResponseAccommodation(
          registration.getParticipant().getUser().getUserId()
          , registration.getRegistrationId()
          , outTextService.getOutTextByKeyAndLangKey(registration.getWorkflowStatus().getLabel(), language.getLanguageKey()).getOutText()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , "Host"
          , hostRegistration.getHostedPersonCount()
          , hostingService.getSlots(hostRegistration, language)
          , ""
          , hostingService.getUtils(hostRegistration, language)
          , hostRegistration.getHostComment()
        )
      );
    });
    hostingService.getAllHosteeRegistrations().forEach(hosteeRegistration -> {
      Registration registration = hosteeRegistration.getRegistration();
      accommodations.add(
        new ResponseAccommodation(
          registration.getParticipant().getUser().getUserId()
          , registration.getRegistrationId()
          , outTextService.getOutTextByKeyAndLangKey(registration.getWorkflowStatus().getLabel(), language.getLanguageKey()).getOutText()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , "Hostee"
          , null
          , hostingService.getSlots(hosteeRegistration, language)
          , hosteeRegistration.getNameRoomMate() == null ? "" : hosteeRegistration.getNameRoomMate()
              + (hosteeRegistration.isSharedBed() ? " (shared bed)" : "")
          , hostingService.getUtils(hosteeRegistration, language)
          , hosteeRegistration.getComment()
        )
      );
    });
    return accommodations;
  }
}
