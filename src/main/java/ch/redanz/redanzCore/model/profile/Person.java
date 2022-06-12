package ch.redanz.redanzCore.model.profile;

import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="person")
public class Person implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="person_id", nullable = false, updatable = false)
  private Long personId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="user_id")
  private User user;

  @Column(name="first_name")
  private String firstName;

  @Column(name="last_name")
  private String lastName;
  private String street;

  @Column(name="street_nr")
  private String streetNr;

  @Column(name="postal_code")
  private String postalCode;
  private String city;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="country_id")
  private Country country;

  @Column(name="person_lang")
  private String personLang;

  @Column(name="update_timestamp")
  private LocalDateTime updateTimestamp;
  private String mobile;

  public Person () {}

  public Person (
    User user,
    String firstName,
    String lastName,
    String street,
    String streetNr,
    String postalCode,
    String city,
    Country country
  ) {
    this.user = user;
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.streetNr = streetNr;
    this.postalCode = postalCode;
    this.city = city;
    this.country = country;
  }

  public Person(User user, String firstName, String lastName, String street, String postalCode, String city) {
    this.user = user;
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
  }

  public Long getPersonId() {
    return personId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getStreet() {
    return street;
  }

  public String getStreetNr() {
    return streetNr;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCity() {
    return city;
  }

  public Country getCountry() {
    return country;
  }

  public String getPersonLang() {
    return personLang;
  }

  public LocalDateTime getUpdateTimestamp() {
    return updateTimestamp;
  }

  public String getMobile() {
    return mobile;
  }

  public User getUser() {
    return user;
  }

  // setter
  public void setUser(User user) {
    this.user = user;
  }

  public void setPersonId(Long personId) {
    this.personId = personId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setStreetNr(String streetNr) {
    this.streetNr = streetNr;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public void setPersonLang(String personLang) {
    this.personLang = personLang;
  }

  public void setUpdateTimestamp(LocalDateTime updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Override
  public String toString(){
    return
      "Person{"
        + "personId=" + personId
        + ", user= '" + user + '\''
        + ", firstName= '" + firstName + '\''
        + ", lastName= '" + lastName + '\''
        + ", street= '" + street + '\''
        + ", streetNr= '" + streetNr + '\''
        + ", postalCode= '" + postalCode + '\''
        + ", city= '" + city + '\''
        + ", country= '" + country + '\''
        + ", personLang= '" + personLang + '\''
        + ", mobile= '" + mobile + '\''
        + ", updateTimestamp= " + updateTimestamp
        + "}";
  }

}
