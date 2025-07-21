package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.*;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PersonService {
  private final PersonRepo personRepo;
  private final CountryService countryService;
  private final LanguageService languageService;

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

  public void submitPerson(PersonResponse personResponse) {
    save(new Person(
       personResponse.getFirstName()
      ,personResponse.getLastName()
      ,personResponse.getStreet()
      ,personResponse.getPostalCode()
      ,personResponse.getCity()
      ,countryService.findCountry(personResponse.getCountryId())
      ,personResponse.getEmail()
      ,personResponse.getMobile()
      ,languageService.findLanguageByLanguageKey(personResponse.getLanguage())
      ,true
    ));
  }

  public void updatePerson(PersonResponse personResponse) {
    Person person = findByPersonId(personResponse.getPersonId());
    person.setFirstName(personResponse.getFirstName());
    person.setLastName(personResponse.getLastName());
    person.setStreet(personResponse.getStreet());
    person.setPostalCode(personResponse.getPostalCode());
    person.setCity(personResponse.getCity());
    person.setCountry(countryService.findCountry(personResponse.getCountryId()));
    person.setEmail(personResponse.getEmail());
    person.setPersonLang(languageService.findLanguageByLanguageKey(personResponse.getLanguage()));
    person.setMobile(personResponse.getMobile());
    personRepo.save(person);
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
