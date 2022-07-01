package ch.redanz.redanzCore.model.reporting.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseRegistration {
  private Long userId;
  private String bundle;
  private String track;
  private String firstName;
  private String lastName;
  private String danceRole;
  private Long partnerUserId;
  private String partnerFirstName;
  private String partnerLastName;
  private String partnerDanceRole;
}
