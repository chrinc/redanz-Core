package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.reporting.response.*;
import ch.redanz.redanzCore.model.reporting.service.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

  @GetMapping(path = "/person/all")
  public List<ResponsePerson> getAllPersonsReport() {

    return reportPersonService.getAllPersonsReport();
  }

  @GetMapping(path = "/registration/all")
  public List<ResponseRegistration> getAllRegistrationsReport() {
    return reportRegistrationService.getAllRegistrationsReport();
  }

  @GetMapping(path = "/registration/open")
  public List<ResponseRegistration> getOpenRegistrationsReport() {
    return reportRegistrationService.getOpenRegistrationsReport();
  }

  @GetMapping(path = "/registration/confirming")
  public List<ResponseRegistration> getConfirmingRegistrationsReport() {
    return reportRegistrationService.getConfirmingRegistrationsReport();
  }

  @GetMapping(path = "/registration/submitted")
  public List<ResponseRegistration> getSubmittedRegistrationsReport() {
    return reportRegistrationService.getSubmittedRegistrationsReport();
  }

  @GetMapping(path = "/registration/done")
  public List<ResponseRegistration> getDoneRegistrationsReport() {
    return reportRegistrationService.getDoneRegistrationsReport();
  }

  @GetMapping(path = "/registration/details")
  public List<ResponseRegistrationDetails> getRegistrationDetailsReport(
    @RequestParam("eventId") Long eventId
  ) {
    return reportRegistrationService.getRegistrationDetailsReport(
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
}
