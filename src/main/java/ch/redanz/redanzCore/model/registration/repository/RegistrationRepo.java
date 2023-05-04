package ch.redanz.redanzCore.model.registration.repository;

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
//    Optional<Registration> findByParticipantAndEvent(Person participant, Event event);
    Optional<Registration> findByParticipantAndEventAndActive(Person participant, Event event, Boolean active);

//    List<Registration> findAllByEvent(Event event);
    List<Registration> findAllByEventAndActive(Event event, Boolean active);
//    List<Registration> findAllByParticipantAndEventArchivedAndEventActive(Person participant, Boolean archived, Boolean active);
    List<Registration> findAllByParticipantAndActiveAndEventArchivedAndEventActive(Person participant, Boolean active, Boolean archived, Boolean eventActive);
//    List<Registration> findAllByParticipant(Person person);
    List<Registration> findAllByParticipantAndActive(Person person, Boolean active);
//    List<Registration> findAllByWorkflowStatusAndEvent(WorkflowStatus workflowStatus, Event event);
    List<Registration> findAllByWorkflowStatusAndActiveAndEvent(WorkflowStatus workflowStatus, Boolean active, Event event);
//    int countAllByWorkflowStatusAndEvent(WorkflowStatus workflowStatus, Event event);
    int countAllByWorkflowStatusAndActiveAndEvent(WorkflowStatus workflowStatus, Boolean active, Event event);
//    int countAllByBundleAndWorkflowStatusAndEvent(Bundle bundle, WorkflowStatus workflowStatus, Event event);
    int countAllByBundleAndWorkflowStatusAndActiveAndEvent(Bundle bundle, WorkflowStatus workflowStatus, Boolean active, Event event);
//    int countAllByTrackAndWorkflowStatusAndEvent(Track track, WorkflowStatus workflowStatus, Event event);
    int countAllByTrackAndWorkflowStatusAndActiveAndEvent(Track track, WorkflowStatus workflowStatus, Boolean active, Event event);
//    int countAllByEvent(Event event);
    int countAllByEventAndActive(Event event, Boolean active);
}
