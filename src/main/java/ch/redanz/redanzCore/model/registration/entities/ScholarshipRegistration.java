package ch.redanz.redanzCore.model.registration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "scholarship_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipRegistration implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "scholarship_registration_id")
  private Long scholarshipRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  private String intro;

  public ScholarshipRegistration(Registration registration, String intro) {
    this.registration = registration;
    this.intro = intro;
  }
}
