package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PersonService {
  private final PersonRepo personRepo;

  public void save(Person person) {
    personRepo.save(person);
  }
  public void delete(Person person) {personRepo.delete(person);}

  public void addPerson(Person person) {
    personRepo.save(person);
  }

  public Person findByUser(User user) {
    return personRepo.findByUser(user);
  }

  public boolean userHasPerson(User user) {
    return personRepo.existsByUser(user);
  }
  public Person findByPersonId(Long personId) {
    return personRepo.findByPersonId(personId);
  }

  public List<Person> findAll(boolean active) {
    return personRepo.findAllByActive(active);
  }

  public void savePerson(Person person) {
    personRepo.save(person);
  }

  public List<Person> getAlOrganizers(Event event) {
    // @Todo, event currently not used;
    return personRepo.findAllByUserUserRole(UserRole.ORGANIZER);
  }
}
