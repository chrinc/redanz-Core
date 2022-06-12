package ch.redanz.redanzCore.model.reporting;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseRegistration {
  private String bundle;
  private String track;
  private String firstName;
  private String lastName;
  private String danceRole;
  private String partnerFirstName;
  private String partnerLastName;
  private String partnerDanceRole;
}
