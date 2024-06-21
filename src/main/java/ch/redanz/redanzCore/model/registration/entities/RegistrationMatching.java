package ch.redanz.redanzCore.model.registration.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Slf4j
@ToString(exclude = {"registration1", "registration2"})
@Getter
@Setter
@Table(name="registration_matching")
public class RegistrationMatching implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="matching_id")
    private Long matchingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_1_id", nullable = false)
    private Registration registration1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_2_id")
    private Registration registration2;

    @Column(name="partnerEmail")
    private String partnerEmail;

    public RegistrationMatching() {}

    public RegistrationMatching(Registration registration1, String partnerEmail) {
        this.registration1 = registration1;
        this.partnerEmail = partnerEmail;
    }
    public RegistrationMatching(Registration registration1) {
        this.registration1 = registration1;
    }
}
