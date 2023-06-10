package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends JpaRepository<Registration, Long> {
    Optional<Registration> findByParticipantAndEventAndActive(Person participant, Event event, Boolean active);
    Registration findByRegistrationId(Long registrationId);
    List<Registration> findAllByEventAndActive(Event event, Boolean active);
    List<Registration> findAllByParticipantAndActiveAndEventArchivedAndEventActive(Person participant, Boolean active, Boolean archived, Boolean eventActive);
    List<Registration> findAllByParticipantAndActive(Person person, Boolean active);
    List<Registration> findAllByWorkflowStatusAndActiveAndEvent(WorkflowStatus workflowStatus, Boolean active, Event event);
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLangAndBundle(
      Boolean active, Event event, WorkflowStatus workflowStatus, Language personLang, Bundle bundle
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLang(
      Boolean active, Event event, WorkflowStatus workflowStatus, Language personLang
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndBundle(
      Boolean active, Event event, WorkflowStatus workflowStatus, Bundle bundle
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatus(
      Boolean active, Event event, WorkflowStatus workflowStatus
    );
    List<Registration> findAllByActiveAndEventAndParticipantPersonLangAndBundle(
      Boolean active, Event event, Language personLang, Bundle bundle
    );
    List<Registration> findAllByActiveAndEventAndParticipantPersonLang(
      Boolean active, Event event, Language personLang
    );
    List<Registration> findAllByActiveAndEventAndBundle(
      Boolean active, Event event, Bundle bundle
    );

    int countAllByWorkflowStatusAndActiveAndEvent(WorkflowStatus workflowStatus, Boolean active, Event event);
    int countAllByBundleAndWorkflowStatusAndActiveAndEvent(Bundle bundle, WorkflowStatus workflowStatus, Boolean active, Event event);
    int countAllByTrackAndWorkflowStatusAndActiveAndEvent(Track track, WorkflowStatus workflowStatus, Boolean active, Event event);
    int countAllByEventAndActive(Event event, Boolean active);
}
