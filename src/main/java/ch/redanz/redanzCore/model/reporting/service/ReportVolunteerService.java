package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.reporting.response.ResponseVolunteer;
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
public class ReportVolunteerService {
  private final VolunteerService volunteerService;
  private final OutTextService outTextService;
  public List<ResponseVolunteer> getVolunteerReport(Language language, Event event) {
    List<ResponseVolunteer> volunteers = new ArrayList<>();
    volunteerService.getAllByEvent(event).forEach(volunteerRegistration -> {
      Registration registration = volunteerRegistration.getRegistration();
      volunteers.add(
        new ResponseVolunteer(
          registration.getParticipant().getUser().getUserId()
          , registration.getRegistrationId()
          , registration.getParticipant().getFirstName()
          , registration.getParticipant().getLastName()
          , registration.getParticipant().getUser().getEmail()
          , registration.getParticipant().getMobile()
          , volunteerRegistration.getType().getDescription()
          , volunteerService.getSlots(volunteerRegistration, language)
          , volunteerRegistration.getIntro()
          , outTextService.getOutTextByKeyAndLangKey(registration.getWorkflowStatus().getLabel(), language.getLanguageKey()).getOutText()

        )
      );

    });
    return volunteers;
  }
}
