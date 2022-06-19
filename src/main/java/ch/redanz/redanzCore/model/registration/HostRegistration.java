package ch.redanz.redanzCore.model.registration;

import ch.redanz.redanzCore.model.profile.User;
import ch.redanz.redanzCore.model.workshop.TypeSlot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="host_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HostRegistration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "host_registration_id")
    private Long hostRegistrationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registration_id")
    @JsonIgnore
    private Registration registration;

    @Column(name = "hosted_person_count")
    private Integer hostedPersonCount;

    @Column(name = "host_comment")
    private String hostComment;

    public HostRegistration(Registration registration, Integer hostedPersonCount, String hostComment) {
        this.registration = registration;
        this.hostedPersonCount = hostedPersonCount;
        this.hostComment = hostComment;
    }
}
