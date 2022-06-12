package ch.redanz.redanzCore.model.service;

import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.reporting.ResponsePerson;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportPersonService {
  private final PersonService personService;

  public List<ResponsePerson> getAllPersonsReport() {
    List<ResponsePerson> personResponseList = new ArrayList<>();
    personService.findAll().forEach(person -> {
      personResponseList.add(
        new ResponsePerson(
          person.getFirstName(),
          person.getLastName(),
          person.getStreet(),
          person.getPostalCode(),
          person.getCity(),
          person.getUser().getEmail()
        )
      );
    });
    return personResponseList;
  }
}
