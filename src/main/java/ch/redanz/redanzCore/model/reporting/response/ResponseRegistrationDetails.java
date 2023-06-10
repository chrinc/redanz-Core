package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseRegistrationDetails {
  private Long userId;
  private Long registrationId;
  private String firstName;
  private String lastName;
  private String email;
  private String bundle;
  private String track;
  private String danceRole;
  private String workflowStatus;
  private String partnerEmail;
  private String languageKey;
  private Long partnerRegistrationId;
  private Long eventId;
  private Long amountDue;
  private Long totalAmount;
}
