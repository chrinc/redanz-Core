package ch.redanz.redanzCore.model.registration;


import ch.redanz.redanzCore.model.workshop.SleepUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name ="host_sleep_util_registration")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HostSleepUtilRegistration implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "host_sleep_util_registration_id")
    private Long hostSleepUtilRegistrationId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="host_registration_id")
    @JsonIgnore
    private HostRegistration hostRegistration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sleep_util_id")
    private SleepUtil sleepUtil;

    @Column(name = "sleep_util_count")
    private Integer sleepUtilCount;

    public HostSleepUtilRegistration(HostRegistration hostRegistration, SleepUtil sleepUtil, Integer sleepUtilCount) {
        this.hostRegistration = hostRegistration;
        this.sleepUtil = sleepUtil;
        this.sleepUtilCount = sleepUtilCount;
    }
}
