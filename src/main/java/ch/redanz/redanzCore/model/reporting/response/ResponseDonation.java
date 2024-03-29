package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseDonation {
  private Long registrationId;
  private String firstName;
  private String lastName;
  private String type;
  private Double amount;
  private String intro;
  private String workflowStatus;
}
