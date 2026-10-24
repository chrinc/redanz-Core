package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// why a registration is (not) released by the release job: one line per level
@Getter
@Setter
public class ResponseReleaseCheck {
  private Long registrationId;
  private boolean eodRelease;     // nightly job active for the event
  private boolean releaseFlag;    // matching ok (set by the job)
  private boolean wouldRelease;   // result of all checks
  private Long pulledByPartnerId; // released together with the partner although own conditions fail
  private String pulledByPartnerName;
  private List<Check> checks = new ArrayList<>();

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Check {
    private String level;           // EVENT, BUNDLE, TRACK, ROLE, PRIVATE_CLASS, SPECIAL, PARTNER
    private String item;
    private boolean ok;
    private boolean pulled;         // fails, but the registration is released together with the partner
    private boolean soldOut;        // effective sold out (as in the overview, incl. full wait list)
    private boolean manual;         // sold out value set manually in the overview
    private String soldOutReason;   // computed sold out: ALL_BUNDLES, ALL_TRACKS, CAPACITY, WAIT_LIST
    private Integer count;          // confirming + done
    private Integer capacity;
    private Integer waitListCount;  // submitted + confirming + done
    private Integer waitListLimit;  // capacity + wait list length
    private String balance;         // dance role balance (leads - follows - switchWeight * switch)
    private String balanceLimit;
    private String partner;         // partner email
    private Long partnerRegistrationId;
  }
}
