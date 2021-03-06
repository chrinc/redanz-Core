package ch.redanz.redanzCore.model.registration.entities;


import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hostee_sleep_util_registration")
public class HosteeSleepUtilRegistration {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "hostee_sleep_util_registration_id")
  private Long hosteeSleepUtilRegistrationId;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "hostee_registration_id")
  @JsonIgnore
  private HosteeRegistration hosteeRegistration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sleep_util_id")
  private SleepUtil sleepUtil;

  public HosteeSleepUtilRegistration(HosteeRegistration hosteeRegistration, SleepUtil sleepUtil) {
    this.hosteeRegistration = hosteeRegistration;
    this.sleepUtil = sleepUtil;
  }
}
