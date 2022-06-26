package ch.redanz.redanzCore.model.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="person")
@Getter
@Setter
@AllArgsConstructor
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

  @Column(name="postal_code")
  private String postalCode;
  private String city;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="country_id")
  private Country country;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="language_key")
  private Language personLang;

  @Column(name="update_timestamp")
  private LocalDateTime updateTimestamp;
  private String mobile;

  public Person () {}

  public Person (
    User user,
    String firstName,
    String lastName,
    String street,
    String postalCode,
    String city,
    Country country,
    Language personLang
  ) {
    this.user = user;
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.country = country;
    this.personLang = personLang;
  }

  public Person(User user, String firstName, String lastName, String street, String postalCode, String city) {
    this.user = user;
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
  }
}
