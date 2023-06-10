package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer_registration")
public class VolunteerRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "volunteer_registration_id")
  private Long volunteerRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  @Column(name = "intro")
  private String intro;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="volunteer_type_id")
  private VolunteerType type;

  public VolunteerRegistration(Registration registration, String intro, VolunteerType type) {
    this.registration = registration;
    this.intro = intro;
    this.type = type;
  }
}
