package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponsePersonRegistrations {
  private Long personId;
  private Long registrationId;
  private String workflowStatus;
  private String bundle;
  private String track;
  private String firstName;
  private String lastName;
  private String email;
  private String mobile;
  private String address;
  private String postalCode;
  private String city;
  private String country;
  private String lang;
  private String role;
}
