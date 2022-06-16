//package ch.redanz.redanzCore.web.restApi.controller;
//
//import ch.redanz.redanzCore.model.profile.response.UserResponse;
//import ch.redanz.redanzCore.model.profile.service.UserRegistrationService;
//import ch.redanz.redanzCore.model.profile.service.UserService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//
//
//@RestController
//@RequestMapping(path="user/registration")
//@AllArgsConstructor
//@Slf4j
//public class UserRegistrationController {
//  private final UserRegistrationService userRegistrationService;
//  private final UserService userService;
//
//  @Autowired
//  private Environment environment;
//
//  @PostMapping
//  public long register(@RequestBody UserResponse request) {
//    userRegistrationService.register(request);
//    return userService.getUser(request.getEmail()).getUserId();
//  }
//
//  @GetMapping(path="confirm")
//  public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
//    userRegistrationService.confirmToken(token);
//
//    // @Todo, exception handler
//    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(environment.getProperty("link.email.confirmed"))).build();
//  }
//}
