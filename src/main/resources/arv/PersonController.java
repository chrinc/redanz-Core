//package ch.redanz.redanzCore.controller;
//
//import ch.redanz.redanzCore.model.profile.entities.Person;
//import ch.redanz.redanzCore.model.profile.service.PersonService;
//import ch.redanz.redanzCore.model.profile.service.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/persons")
//public class PersonController {
//
//  private final PersonService personService;
//  private final UserService userService;
//
//  public PersonController(PersonService personService, UserService userService) {
//    this.personService = personService;
//    this.userService = userService;
//  }
//
////  @PostMapping("/add")
////  public ResponseEntity<Person> addPerson(@RequestBody Person person){
////    Logger logger = Logger.getLogger(PersonController.class.getName());
////    logger.log(Level.WARNING, "inc, person: " + person.toString());
////
////    // Save Person
//////    Person newPerson = personService.addPerson(person);
//////    return new ResponseEntity<>(newPerson, HttpStatus.CREATED);
////  }
//}
