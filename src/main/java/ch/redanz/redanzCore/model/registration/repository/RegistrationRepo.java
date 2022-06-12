package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends JpaRepository<Registration, Long> {
    Optional<Registration> findByParticipantAndEvent(Person participant, Event event);
    List<Registration> findAllByEvent(Event event);
//    Registration findByParticipantIdAndEventId(Long personId);
}
