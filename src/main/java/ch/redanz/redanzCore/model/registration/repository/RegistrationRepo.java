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
import org.springframework.data.repository.query.Param;
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
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLangAndBundleAndRegistrationType(
      Boolean active, Event event, WorkflowStatus workflowStatus, Language personLang, Bundle bundle, RegistrationType type
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLangAndRegistrationType(
      Boolean active, Event event, WorkflowStatus workflowStatus, Language personLang, RegistrationType type
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndBundleAndRegistrationType(
      Boolean active, Event event, WorkflowStatus workflowStatus, Bundle bundle, RegistrationType type
    );
    List<Registration> findAllByActiveAndEventAndWorkflowStatusAndRegistrationType(
      Boolean active, Event event, WorkflowStatus workflowStatus, RegistrationType type
    );
    List<Registration> findAllByActiveAndEventAndParticipantPersonLangAndBundleAndRegistrationType(
      Boolean active, Event event, Language personLang, Bundle bundle, RegistrationType type
    );
    List<Registration> findAllByActiveAndEventAndParticipantPersonLangAndRegistrationType(
      Boolean active, Event event, Language personLang, RegistrationType type
    );
    List<Registration> findAllByActiveAndEventAndBundleAndRegistrationType(
      Boolean active, Event event, Bundle bundle, RegistrationType type
    );

    boolean existsByActiveAndEventAndParticipant(Boolean active, Event event, Person participant);

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

    @Query("SELECT CONCAT(p.firstName, ' ', p.lastName) FROM Registration r INNER JOIN r.participant p WHERE r.registrationId = :registrationId")
    String getFullName(@Param("registrationId") Long registrationId);

    @Query("SELECT b.name FROM Registration r INNER JOIN r.bundle b WHERE r.registrationId = :registrationId")
    String getBundleName(@Param("registrationId") Long registrationId);

    @Query("SELECT t.name FROM Registration r LEFT JOIN r.track t WHERE r.registrationId = :registrationId")
    String getTrackName(@Param("registrationId") Long registrationId);

    @Query("SELECT wfs.name FROM Registration r LEFT JOIN r.workflowStatus wfs WHERE r.registrationId = :registrationId")
    String getWorkflowStatusName(@Param("registrationId") Long registrationId);

    @Query("SELECT b.price FROM Registration r LEFT JOIN r.bundle b WHERE r.registrationId = :registrationId")
    int getBundlePrice(@Param("registrationId") Long registrationId);
}
