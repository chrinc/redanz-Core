package ch.redanz.redanzCore.model.registration;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Slf4j
@ToString
@Table(name="registration_matching")
public class RegistrationMatching implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="matching_id")
    private Long matchingId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_1_id", nullable = false)
    private Registration registration1;
//
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registration_2_id", nullable = true)
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

    public Long getMatchingId() {
        return matchingId;
    }

    public void setMatchingId(Long matchingId) {
        this.matchingId = matchingId;
    }

    public void setRegistration1(Registration registration1) {
        this.registration1 = registration1;
    }

    public Registration getRegistration1() {
        return registration1;
    }

    public Registration getRegistration2() {
        return registration2;
    }

    public void setRegistration2(Registration registration2) {
        this.registration2 = registration2;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }
}
