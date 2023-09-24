package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.SpecialRegistrationService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.reporting.response.ResponseSpecials;
import ch.redanz.redanzCore.model.reporting.response.ResponseVolunteer;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ReportSpecialsService {
  private final PrivateClassService privateClassService;
  private final OutTextService outTextService;
  private final FoodRegistrationService foodRegistrationService;
  private final SpecialRegistrationService specialRegistrationService;
  public List<ResponseSpecials> getSpecialsReport(Language language, Event event) {
    List<ResponseSpecials> specials = new ArrayList<>();
    Set<Registration> registrations = foodRegistrationService.getRegistrationsByEvent(event);
    registrations.addAll(specialRegistrationService.getRegistrationsByEvent(event));
    registrations.addAll(privateClassService.getRegistrationsByEvent(event));

    registrations.forEach(registration -> {
      specials.add(
        new ResponseSpecials(
          registration.getParticipant().getPersonId()
          , registration.getRegistrationId()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , foodRegistrationService.getReportFoodSlots(registration, language)
          , privateClassService.getReportPrivates(registration, language)
          , specialRegistrationService.getReportSpecials(registration, language)
          , outTextService.getOutTextByKeyAndLangKey(registration.getWorkflowStatus().getLabel(), language.getLanguageKey()).getOutText()
        )
      );

    });
    return specials;
  }
}
