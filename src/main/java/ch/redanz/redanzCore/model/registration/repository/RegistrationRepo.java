package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
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
    Optional<Registration> findByParticipantAndEventAndRegistrationTypeAndActive(Person participant, Event event, RegistrationType registrationType, Boolean active);
    Registration findByRegistrationId(Long registrationId);
    List<Registration> findAllByEventAndActiveAndRegistrationType(Event event, Boolean active, RegistrationType type);
    List<Registration> findAllByParticipantAndActiveAndEventArchivedAndEventActive(Person participant, Boolean active, Boolean archived, Boolean eventActive);
    List<Registration> findAllByParticipantAndActive(Person person, Boolean active);
    List<Registration> findAllByWorkflowStatusAndActiveAndEventAndRegistrationType(WorkflowStatus workflowStatus, Boolean active, Event event, RegistrationType type);
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLangAndBundle(
      Boolean active, Event event, WorkflowStatus workflowStatus, Language personLang, Bundle bundle
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLang(
      Boolean active, Event event, WorkflowStatus workflowStatus, Language personLang
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndBundle(
      Boolean active, Event event, WorkflowStatus workflowStatus, Bundle bundle
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndRegistrationType(
      Boolean active, Event event, WorkflowStatus workflowStatus, RegistrationType type
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

    int countAllByWorkflowStatusAndActiveAndEventAndRegistrationType(WorkflowStatus workflowStatus, Boolean active, Event event, RegistrationType type);
    int countAllByBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(Bundle bundle, WorkflowStatus workflowStatus, Boolean active, Event event, RegistrationType type);
    int countAllByTrackAndWorkflowStatusAndActiveAndEventAndRegistrationType(Track track, WorkflowStatus workflowStatus, Boolean active, Event event, RegistrationType type);
    int countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(Track track, Bundle bundle, WorkflowStatus workflowStatus, Boolean active, Event event, RegistrationType type);
    int countAllByTrackAndWorkflowStatusAndDanceRoleAndActiveAndEventAndRegistrationType(Track track, WorkflowStatus workflowStatus, DanceRole danceRole, Boolean active, Event event, RegistrationType type);
    int countAllByTrackAndWorkflowStatusAndDanceRoleAndActiveAndEventAndRegistrationTypeAndBundle(Track track, WorkflowStatus workflowStatus, DanceRole danceRole, Boolean active, Event event, RegistrationType type, Bundle bundle);
    int countAllByEventAndActiveAndRegistrationType(Event event, Boolean active, RegistrationType type);
    int countAllByEventAndActive(Event event, Boolean active);
    int countAllByWorkflowStatusAndActiveAndEventAndDanceRole(WorkflowStatus workflowStatus, Boolean active, Event event, DanceRole danceRole);
    int countAllByBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(Bundle bundle, WorkflowStatus workflowStatus, Boolean active, Event event, DanceRole danceRole);
    int countAllByTrackAndWorkflowStatusAndActiveAndEventAndDanceRole(Track track, WorkflowStatus workflowStatus, Boolean active, Event event, DanceRole danceRole);
    int countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(Track track, Bundle bundle, WorkflowStatus workflowStatus, Boolean active, Event event, DanceRole danceRole);
    int countAllByEventAndActiveAndDanceRole(Event event, Boolean active, DanceRole danceRole);
    int countAllByEvent(Event event);
}
