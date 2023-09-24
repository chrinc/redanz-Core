package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseVolunteer {
  private Long personId;
  private Long registrationId;
  private String firstName;
  private String lastName;
  private String email;
  private String mobile;
  private String type;
  private String slots;
  private String intro;
  private String workflowStatus;
}
