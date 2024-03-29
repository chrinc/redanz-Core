package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("core-api/app/email")
@AllArgsConstructor
public class EmailController {
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;

  @GetMapping(path = "/send-generic-email")
  public void sendGenericMail(
    @RequestParam("senderUserId")  Long senderUserId,
    @RequestParam("registrationId") Long registrationId,
    @RequestParam("emailContent") String emailContent
  ) {
    try {
      JsonObject jsonEmail = JsonParser.parseString(emailContent).getAsJsonObject();
      Boolean allWithBundle =
        jsonEmail.get("allWithBundle") != null && (
          !jsonEmail.get("allWithBundle").isJsonNull() && jsonEmail.get("allWithBundle").getAsBoolean()
        );
      Boolean allWithLang =
        jsonEmail.get("allWithLang") != null && (
          !jsonEmail.get("allWithLang").isJsonNull() && jsonEmail.get("allWithLang").getAsBoolean()
        );
      Boolean allStatus =
        jsonEmail.get("allStatus") != null && (
          !jsonEmail.get("allStatus").isJsonNull() && jsonEmail.get("allStatus").getAsBoolean()
        );

      Registration registration = registrationService.findByRegistrationId(registrationId);

      List<Registration> registrationList = new ArrayList<Registration>();
      if (allWithBundle || allWithLang || allStatus) {
        registrationList = allStatus && allWithLang && allWithBundle ?
          registrationService.findAllByEventStatusLangAndBundle(
            registration.getEvent(), registration.getWorkflowStatus(), registration.getParticipant().getPersonLang(), registration.getBundle()
          )
          : allStatus && allWithLang ?
          registrationService.findAllByEventStatusAndLang(
            registration.getEvent(), registration.getWorkflowStatus(), registration.getParticipant().getPersonLang()
          )
          : allStatus && allWithBundle ?
          registrationService.findAllByEventStatusAndBundle(
            registration.getEvent(), registration.getWorkflowStatus(), registration.getBundle()
          )
          : allStatus ?
          registrationService.findAllByEventAndStatus(
            registration.getEvent(), registration.getWorkflowStatus()
          )
          : allWithLang && allWithBundle ?
          registrationService.findAllByEventLangAndBundle(
            registration.getEvent(), registration.getParticipant().getPersonLang(), registration.getBundle()
          )
          : allWithLang ?
          registrationService.findAllByEventAndLang(
            registration.getEvent(), registration.getParticipant().getPersonLang()
          )
          : allWithBundle ?
          registrationService.findAllByEventAndBundle(
            registration.getEvent(), registration.getBundle()
          )
          : null;

      } else {
        registrationList.add(registration);
      }

      registrationEmailService.sendGenericEmail(
        senderUserId,
        registrationList,
        jsonEmail
      );

    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
