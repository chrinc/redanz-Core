package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponsePerson;
import ch.redanz.redanzCore.model.reporting.response.ResponsePersonRegistrations;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportPersonService {
  private final PersonService personService;
  private final RegistrationService registrationService;
  private final OutTextService outTextService;

  public List<ResponsePerson> getAllPersonsReport() {
    List<ResponsePerson> personResponseList = new ArrayList<>();
    personService.findAll(true).forEach(person -> {
      personResponseList.add(
        new ResponsePerson(
          person.getFirstName(),
          person.getLastName(),
          person.getStreet(),
          person.getPostalCode(),
          person.getCity(),
          person.getEmail(),
          person.getUser() == null ? "NO USER" : person.getUser().getUserRole().toString(),
          person.getPersonLang().getLanguageKey(),
          person.getPersonId()
        )
      );
    });
    return personResponseList;
  }

  public List<ResponsePersonRegistrations> getPersonResponseReport(Language language, Event event) {
    List<ResponsePersonRegistrations> personRegistrations = new ArrayList<>();

    registrationService.findAllByEvent(event).forEach(registration -> {
      personRegistrations.add(
        new ResponsePersonRegistrations(
          registration.getParticipant().getPersonId()
          , registration.getRegistrationId()
          , outTextService.getOutTextByKeyAndLangKey(registration.getWorkflowStatus().getLabel(), language.getLanguageKey()).getOutText()
          , registration.getBundle().getName()
          , registration.getTrack() == null ? null : registration.getTrack().getName()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , registration.getParticipant().getEmail()
          , registration.getParticipant().getMobile()
          , registration.getParticipant().getStreet()
          , registration.getParticipant().getPostalCode()
          , registration.getParticipant().getCity()
          , registration.getParticipant().getCountry().getName()
          , registration.getParticipant().getPersonLang().getLanguageKey()
          , registration.getDanceRole() == null ? null : registration.getDanceRole().getName()
        )
      );

    });
    return personRegistrations;
  }
}
