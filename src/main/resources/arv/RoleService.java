//package ch.redanz.redanzCore.service;
//
//import ch.redanz.redanzCore.user.Role;
//import ch.redanz.redanzCore.repo.RoleRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RoleService {
//  private final RoleRepo roleRepo;
//
//  @Autowired
//  public RoleService(RoleRepo roleRepo) {
//    this.roleRepo = roleRepo;
//  }
//
//  public Role findRoleByInternalId(String roleId){
//    return roleRepo.findRoleByInternalId(roleId);
//  }
//}
