//package ch.redanz.redanzCore.controller;
//
//import ch.redanz.redanzCore.user.Role;
//import ch.redanz.redanzCore.domain.User;
//import ch.redanz.redanzCore.service.RoleService;
//import ch.redanz.redanzCore.model.profile.service.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/users")
//
//public class UserResource {
//
//  private final UserService userService;
//  private final RoleService roleService;
//
//  public UserResource(UserService userService, RoleService roleService) {
//    this.userService = userService;
//    this.roleService = roleService;
//  }
//
//  @GetMapping("/all")
//  public ResponseEntity<List<User>> getAllUsers(){
//    List<User> userList = userService.findAllUsers();
//    Logger logger = Logger.getLogger(UserResource.class.getName());
//    logger.log(Level.WARNING, "inc, return userList: " + userList.toString());
//
//    return new ResponseEntity<>(userList, HttpStatus.OK);
//  }
//
//  @GetMapping("/find/{userId}")
//  public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
//    User user = userService.findUserByUserId(userId);
//    return new ResponseEntity< >(user, HttpStatus.OK);
//  }
//
//  @PostMapping("/add")
//  public ResponseEntity<User> addUser(@RequestBody User user){
//    Logger logger = Logger.getLogger(UserResource.class.getName());
//    logger.log(Level.WARNING, "inc, user: " + user.toString());
//
//    // Foreign Key Entity Lookup
//    // @todo: hardwire participant role here
//    Role role = roleService.findRoleByInternalId(user.getRole().getInternalId());
//
//    // Set Existing Role
//    user.setRole(role);
//
//    logger.log(Level.WARNING, "inc, userKey: " + user.getUserKey());
//    if (user.getUserKey()==null) {
//      user.setUserKey(UUID.randomUUID().toString());
//    }
//    // Save User
//    logger.log(Level.WARNING, "inc, add user");
//    User newUser = userService.addUser(user);
//    logger.log(Level.WARNING, "inc, user added return?");
//    return new ResponseEntity<>(newUser, HttpStatus.CREATED);
//  }
//
//  @PutMapping("/update")
//  public ResponseEntity<User> updateUser(@RequestBody User user){
//    User updateUser = userService.updateUser(user);
//    return new ResponseEntity<>(updateUser, HttpStatus.CREATED);
//  }
//
////  @DeleteMapping("/delete/{userId}")
////  public ResponseEntity<?> deleteUser(@PathVariable Long userId){
////    userService.deleteUser(userId);
////    return new ResponseEntity<>(HttpStatus.OK);
////  }
//}
