package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseAccommodation {
  private Long personId;
  private Long registrationId;
  private String workflowStatus;
  private String firstName;
  private String lastName;
  private String host;
  private Integer personCount;
  private String slots;
  private String namePartner;
  private String utilities;
  private String address;
  private String city;
  private String comments;
  private String registrationType;
}
