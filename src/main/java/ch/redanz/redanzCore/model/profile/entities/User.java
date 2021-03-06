package ch.redanz.redanzCore.model.profile.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;


@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable, UserDetails {
  @SequenceGenerator(
    name = "generator",
    sequenceName = "generator",
    allocationSize = 1
  )
  @Id
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "generator"
  )
  @JoinColumn(name = "user_id")
  private Long userId;
  @JsonIgnore
  private String email;
  @JsonIgnore
  private String password;


  public User(
    String email,
    String password,
    UserRole userRole,
    Boolean locked,
    Boolean enabled) {
    this.email = email;
    this.password = password;
    this.userRole = userRole;
    this.locked = locked;
    this.enabled = enabled;
  }

  @Column(name = "user_role")
  @Enumerated(EnumType.STRING)
  private UserRole userRole;
  @JsonIgnore
  private Boolean locked = false;
  @JsonIgnore
  private Boolean enabled = false;

//  public User () {}

  public User(
    String email,
    String password,
    UserRole userRole) {
    this.email = email;
    this.password = password;
    this.userRole = userRole;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
    return Collections.singleton(authority);
  }

  public String getPassword() {
    return password;
  }

  // implements UserDetails
  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return enabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  //   setter
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return
      "User{"
        + "userId=" + userId + '\''
        + ", ch.redanz.redanzCore.email=" + email + '\''
        + ", password= '" + password + '\''
        + ", role= '" + userRole
        + "}";
  }

}

