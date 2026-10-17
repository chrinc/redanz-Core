package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.DonationRegistrationService;
import ch.redanz.redanzCore.model.registration.service.HostingService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.service.email.EmailAccountService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import com.google.gson.JsonArray;
import org.springframework.transaction.annotation.Transactional;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("core-api/app/email")
@AllArgsConstructor
public class EmailController {
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;
  private final PersonService personService;
  private final DonationRegistrationService donationRegistrationService;
  private final OutTextService outTextService;
  private final HostingService hostingService;
  private final WorkflowStatusService workflowStatusService;
  private final EmailAccountService emailAccountService;

  // email accounts (static data), admins only (see CustomAuthorizationFilter)
  @GetMapping(path = "/accounts")
  public List<Map<String, Object>> getAccounts() {
    return emailAccountService.getAll();
  }

  @PostMapping(path = "/accounts/upsert")
  @Transactional
  public void upsertAccounts(@RequestBody String jsonString) {
    try {
      emailAccountService.upsert(JsonParser.parseString(jsonString).getAsJsonArray());
    } catch (ApiRequestException apiRequestException) {
      throw apiRequestException;
    } catch (Exception exception) {
      log.error("email account upsert failed", exception);
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping(path = "/accounts/test")
  public List<String> testAccount(@RequestBody String jsonString) {
    return emailAccountService.testConnection(JsonParser.parseString(jsonString).getAsJsonObject());
  }

  // sender options for the email form
  @GetMapping(path = "/senders")
  public List<Map<String, String>> getSenders() {
    return emailAccountService.getSenders();
  }

  // context of a registration for the email component (labels of the recipient filters)
  @GetMapping(path = "/registration-context")
  public Map<String, Object> getRegistrationContext(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      Registration registration = registrationService.findByRegistrationId(registrationId);
      Map<String, Object> context = new HashMap<>();
      context.put("registrationId", registration.getRegistrationId());
      context.put("workflowStatus", outTextService.getOutTextMapByKey(registration.getWorkflowStatus().getLabel()));
      context.put("bundle", registration.getBundle() == null ? null : registration.getBundle().getName());
      context.put("languageKey", registration.getParticipant().getPersonLang() == null ? null : registration.getParticipant().getPersonLang().getLanguageKey());
      context.put("donor", donationRegistrationService.isDonor(registration));
      context.put("host", hostingService.hasHostRegistration(registration));
      context.put("hostee", hostingService.hasHosteeRegistration(registration));
      return context;
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/send-generic-email")
  public void sendGenericMail(
    @RequestParam("senderUserId")  Long senderUserId,
    @RequestParam("registrationId") Long registrationId,
    @RequestParam("emailContent") String emailContent
  ) {
    try {
      JsonObject jsonEmail = JsonParser.parseString(emailContent).getAsJsonObject();
      registrationEmailService.sendGenericEmail(
        senderUserId,
        recipients(registrationId, jsonEmail),
        jsonEmail
      );

    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  // number of recipients (frontend: hint if more than 500 emails)
  @GetMapping(path = "/send-generic-email/count")
  public int countGenericMailRecipients(
    @RequestParam("registrationId") Long registrationId,
    @RequestParam("emailContent") String emailContent
  ) {
    try {
      return recipients(registrationId, JsonParser.parseString(emailContent).getAsJsonObject()).size();
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  // recipients of a generic email: the registration, or all matching the flags (intersection)
  private List<Registration> recipients(Long registrationId, JsonObject jsonEmail) {
    Boolean allWithBundle =
      jsonEmail.get("allWithBundle") != null ?
        (
          !jsonEmail.get("allWithBundle").isJsonNull() ?
            jsonEmail.get("allWithBundle").getAsBoolean() : false
        )
        : false;
    Boolean allWithLang =
      jsonEmail.get("allWithLang") != null ?
        (
          !jsonEmail.get("allWithLang").isJsonNull() ?
            jsonEmail.get("allWithLang").getAsBoolean() : false
        )
        : false;
    Boolean allStatus =
      jsonEmail.get("allStatus") != null ?
        (
          !jsonEmail.get("allStatus").isJsonNull() ?
            jsonEmail.get("allStatus").getAsBoolean() : false
        )
        : false;
    Boolean allDonors =
      jsonEmail.get("allDonors") != null ?
        (
          !jsonEmail.get("allDonors").isJsonNull() ?
            jsonEmail.get("allDonors").getAsBoolean() : false
        )
        : false;
    Boolean allHosts = getFlag(jsonEmail, "allHosts");
    Boolean allHostees = getFlag(jsonEmail, "allHostees");
    Registration registration = registrationService.findByRegistrationId(registrationId);

    // several flags: the intersection applies
    List<Registration> registrationList = new ArrayList<Registration>();
    if (allWithBundle || allWithLang || allStatus || allDonors || allHosts || allHostees) {
      registrationList = null;
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
      }

      // donors, hosts, hostees: only registrations with status done
      Long doneStatusId = workflowStatusService.getDone().getWorkflowStatusId();
      if (allDonors) {
        registrationList = intersect(registrationList,
          donationRegistrationService.allDonors(registration.getEvent()).stream()
            .filter(r -> r.getWorkflowStatus().getWorkflowStatusId().equals(doneStatusId))
            .collect(Collectors.toList())
        );
      }
      if (allHosts) {
        registrationList = intersect(registrationList,
          hostingService.getAllHostRegistrationsByEvent(registration.getEvent()).stream()
            .map(hostRegistration -> hostRegistration.getRegistration())
            .filter(r -> r.getWorkflowStatus().getWorkflowStatusId().equals(doneStatusId))
            .collect(Collectors.toList())
        );
      }
      if (allHostees) {
        registrationList = intersect(registrationList,
          hostingService.getAllHosteeRegistrationsByEvent(registration.getEvent()).stream()
            .map(hosteeRegistration -> hosteeRegistration.getRegistration())
            .filter(r -> r.getWorkflowStatus().getWorkflowStatusId().equals(doneStatusId))
            .collect(Collectors.toList())
        );
      }

    } else {
      registrationList.add(registration);
    }
    return registrationList;
  }

  private static Boolean getFlag(JsonObject jsonEmail, String key) {
    return jsonEmail.get(key) != null && !jsonEmail.get(key).isJsonNull() && jsonEmail.get(key).getAsBoolean();
  }

  // null = no restriction yet
  private static List<Registration> intersect(List<Registration> registrationList, List<Registration> other) {
    if (registrationList == null) {
      return other.stream().distinct().collect(Collectors.toList());
    }
    Set<Long> otherIds = other.stream().map(Registration::getRegistrationId).collect(Collectors.toSet());
    return registrationList.stream()
      .filter(r -> otherIds.contains(r.getRegistrationId()))
      .collect(Collectors.toList());
  }

  @PostMapping(path = "/send-generic-mass-email")
  public void sendGenericMassMail(@RequestBody Map<String, Object> payload) {
    try {

      Long senderUserId = Long.valueOf(payload.get("senderUserId").toString());
      Map<String, Object> emailContent = (Map<String, Object>) payload.get("emailContent");
      List<Map<String, Object>> receiverList = (List<Map<String, Object>>) payload.get("receiverList");
      receiverList.forEach(receiver -> {
        try {
          registrationEmailService.sendGenericEmail(
            senderUserId,
            personService.findByPersonId(Long.valueOf(receiver.get("personId").toString())),
            emailContent.get("subject").toString(),
            emailContent.get("content").toString(),
            null,
            null
          );
        } catch (IOException e) {
          throw new RuntimeException(e);
        } catch (TemplateException e) {
          throw new RuntimeException(e);
        }
      });

    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
