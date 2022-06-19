package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.profile.User;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PersonService {
  private final PersonRepo personRepo;

//  @Autowired
//  public PersonService(PersonRepo personRepo) {
//    this.personRepo = personRepo;
//  }

  public void addPerson(Person person) {
    personRepo.save(person);
  }
  public Person findByUser(User user) {return personRepo.findByUser(user);}
  public Person findByPersonId(Long personId) {return personRepo.findByPersonId(personId);}
  public List<Person> findAll() {return personRepo.findAll();}

  public void savePerson(Person person) {personRepo.save(person);};
}
