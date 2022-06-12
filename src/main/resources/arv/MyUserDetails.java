//package ch.redanz.redanzCore.service;
//
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Arrays;
//import java.util.Collection;
//
//@EnableWebSecurity
//public class MyUserDetails implements UserDetails {
//  private String userName;
//
//  public MyUserDetails(String userName) {
//    this.userName = userName;
//  }
//  public MyUserDetails() {
//  }
//
//  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return Arrays.asList(new SimpleGrantedAuthority("ADMIN"));
//  }
//
//  @Override
//  public String getPassword() {
//    return "pass";
//  }
//
//  @Override
//  public String getUsername() {
//    return userName;
//  }
//
//  @Override
//  public boolean isAccountNonExpired() {
//    return true;
//  }
//
//  @Override
//  public boolean isAccountNonLocked() {
//    return true;
//  }
//
//  @Override
//  public boolean isCredentialsNonExpired() {
//    return true;
//  }
//
//  @Override
//  public boolean isEnabled() {
//    return true;
//  }
//}
