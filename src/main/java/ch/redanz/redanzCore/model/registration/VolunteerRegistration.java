package ch.redanz.redanzCore.model.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="volunteer_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "volunteer_registration_id")
  private Long volunteerRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="registration_id")
  @JsonIgnore
  private Registration registration;

  @Column(name = "intro")
  private String intro;

  public VolunteerRegistration(Registration registration, String intro) {
    this.registration = registration;
    this.intro = intro;
  }
}
