package ch.redanz.redanzCore.model.profile.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "person")
@Getter
@Setter
@AllArgsConstructor
public class Person implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "person_id", nullable = false, updatable = false)
  private Long personId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String email;

  private String street;

  @Column(name = "postal_code")
  private String postalCode;
  private String city;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "country_id")
  private Country country;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "language_key")
  private Language personLang;

  @Column(name = "update_timestamp")
  private LocalDateTime updateTimestamp;
  private String mobile;

  private boolean active;

  public Person() {
  }


  public Person(
    User user,
    String firstName,
    String lastName,
    String street,
    String postalCode,
    String city,
    Country country,
    Language personLang,
    String email,
    boolean active
  ) {
    this.user = user;
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.country = country;
    this.personLang = personLang;
    this.email = email;
    this.active = active;
  }

  public Person(User user, String firstName, String lastName, String street, String postalCode, String city, String email, boolean active) {
    this.user = user;
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.email = email;
    this.active = active;
  }

  public Person(String firstName, String lastName, String street, String postalCode, String city, Country country, String email, String mobile, Language language, boolean active) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.email = email;
    this.mobile = mobile;
    this.personLang = language;
    this.country = country;
    this.active = active;
  }
}
