package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends JpaRepository<Registration, Long> {
    Optional<Registration> findByParticipantAndEvent(Person participant, Event event);
    List<Registration> findAllByEvent(Event event);
    List<Registration> findAllByParticipantAndEventArchivedAndEventActive(Person participant, Boolean archived, Boolean active);
    List<Registration> findAllByParticipant(Person person);
    List<Registration> findAllByWorkflowStatusAndEvent(WorkflowStatus workflowStatus, Event event);
    int countAllByWorkflowStatusAndEvent(WorkflowStatus workflowStatus, Event event);
    int countAllByBundleAndWorkflowStatusAndEvent(Bundle bundle, WorkflowStatus workflowStatus, Event event);
    int countAllByTrackAndWorkflowStatusAndEvent(Track track, WorkflowStatus workflowStatus, Event event);
    int countAllByEvent(Event event);
}
