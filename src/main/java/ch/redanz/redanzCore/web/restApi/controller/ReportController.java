package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.reporting.ResponsePerson;
import ch.redanz.redanzCore.model.reporting.ResponseRegistration;
import ch.redanz.redanzCore.model.service.ReportPersonService;
import ch.redanz.redanzCore.model.service.ReportRegistrationService;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("core-api/app/report")
public class ReportController {

  private final ReportRegistrationService reportRegistrationService;
  private final ReportPersonService reportPersonService;


  @Autowired
  Configuration mailConfig;

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
}
