package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.service.CheckInService;
import ch.redanz.redanzCore.model.registration.service.GuestService;
import ch.redanz.redanzCore.model.reporting.response.*;
import ch.redanz.redanzCore.model.reporting.service.*;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("core-api/app/report")
public class ReportController {

  private final ReportRegistrationService reportRegistrationService;
  private final ReportPersonService reportPersonService;
  private final ReportVolunteerService reportVolunteerService;
  private final ReportAccommodationService reportAccommodationService;
  private final ReportStatsService reportStatsService;
  private final LanguageService languageService;
  private final EventService eventService;
  private final ReportDonationService reportDonationService;
  private final ReportSpecialsService reportSpecialsService;
  private final ReportEmailLogsService reportEmailLogsService;
  private final GuestService  guestService;
  private final CheckInService checkInService;
  private final ReportCheckinService reportCheckinService;
  private final SlotService slotService;

  @GetMapping(path = "/person/all")
  public List<ResponsePerson> getAllPersonsReport() {
    return reportPersonService.getAllPersonsReport();
  }

  @GetMapping(path = "/person/registrations")
  public List<ResponsePersonRegistrations> getPersonRegistrations(
    @RequestParam("languageKey") String languageKey,
    @RequestParam("eventId") Long eventId
  ) {
    return reportPersonService.getPersonResponseReport(
      languageService.findLanguageByLanguageKey(languageKey.toUpperCase()),
      eventService.findByEventId(eventId)
    );
  }
//
//  @GetMapping(path = "/registration/all")
//  public List<ResponseRegistration> getAllRegistrationsReport() {
//    return reportRegistrationService.getAllRegistrationsReport();
//  }
//
//  @GetMapping(path = "/registration/open")
//  public List<ResponseRegistration> getOpenRegistrationsReport() {
//    return reportRegistrationService.getOpenRegistrationsReport();
//  }
//
//  @GetMapping(path = "/registration/confirming")
//  public List<ResponseRegistration> getConfirmingRegistrationsReport() {
//    return reportRegistrationService.getConfirmingRegistrationsReport();
//  }
//
//  @GetMapping(path = "/registration/submitted")
//  public List<ResponseRegistration> getSubmittedRegistrationsReport() {
//    return reportRegistrationService.getSubmittedRegistrationsReport();
//  }
//
//  @GetMapping(path = "/registration/done")
//  public List<ResponseRegistration> getDoneRegistrationsReport() {
//    return reportRegistrationService.getDoneRegistrationsReport();
//  }

  @GetMapping(path = "/registration/details")
  public List<ResponseRegistrationDetails> getRegistrationDetailsReport(
    @RequestParam("eventId") Long eventId
  ) {
    return reportRegistrationService.getRegistrationDetailsReport(
      eventService.findByEventId(eventId)
    );
  }

  @GetMapping(path = "/registration/emailLogs")
  public List<ResponseEmailLogs> getEmailLogs(
    @RequestParam("eventId") Long eventId
  ) {
    return reportEmailLogsService.getEmailLogs(
      eventService.findByEventId(eventId)
    );
  }

  @GetMapping(path = "/volunteer/all")
  public List<ResponseVolunteer> getVolunteerReport(
    @RequestParam("languageKey") String languageKey,
    @RequestParam("eventId") Long eventId
  ) {
    return reportVolunteerService.getVolunteerReport(
      languageService.findLanguageByLanguageKey(languageKey.toUpperCase()),
      eventService.findByEventId(eventId)
    );
  }

  @GetMapping(path = "/special/all")
  public List<ResponseSpecials> getSpecialsReport(
    @RequestParam("languageKey") String languageKey,
    @RequestParam("eventId") Long eventId
  ) {
    return reportSpecialsService.getSpecialsReport(
      languageService.findLanguageByLanguageKey(languageKey.toUpperCase()),
      eventService.findByEventId(eventId)
    );
  }

  @GetMapping(path = "/accommodation/all")
  public List<ResponseAccommodation> getAccommodationReport(
    @RequestParam("languageKey") String languageKey,
    @RequestParam("eventId") Long eventId
  ) {
    return reportAccommodationService.getAccommodationReport(
      languageService.findLanguageByLanguageKey(languageKey.toUpperCase()),
      eventService.findByEventId(eventId)
    );
  }
  @GetMapping(path = "/donation/all")
  public List<ResponseDonation> getDonationReport(
    @RequestParam("languageKey") String languageKey,
    @RequestParam("eventId") Long eventId
  ) {
    return reportDonationService.getDonationReport(
      languageService.findLanguageByLanguageKey(languageKey.toUpperCase()),
      eventService.findByEventId(eventId)
    );
  }

  @GetMapping(path = "/stats")
  public List<ResponseStats> getStatsReport(
    @RequestParam("languageKey") String languageKey,
    @RequestParam("eventId") Long eventId
  ) {
    // log.info("/stats, eventId: " + eventId);
    // log.info("/stats, languageKey: " + languageKey);
    return reportStatsService.getStatsReport(
      languageService.findLanguageByLanguageKey(languageKey.toUpperCase()),
      eventService.findByEventId(eventId)
    );
  }

  @GetMapping (path = "/guest/all")
  @Transactional
  public List<Guest> guestList(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      return guestService.findAllByEvent(event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping (path = "/checkIn/all")
  @Transactional
  public List<ResponseCheckIn> getAllCheckIns(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      return reportCheckinService.getCheckinReport(event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping (path = "/checkIn/slots")
  @Transactional
  public List<Slot> getCheckInSlots(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      return slotService.getAllSlots("Party", event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping(path = "/schema/guest")
  public List<Map<String, String>> getGuestSchema() {
    return guestService.getSchema();
  }

  @GetMapping(path = "/schema/checkIn")
  public List<Map<String, String>> getCheckInSchema() {
    return ResponseCheckIn.schema();
  }
}
