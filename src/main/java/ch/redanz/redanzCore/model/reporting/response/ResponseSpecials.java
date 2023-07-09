package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseSpecials {
  private Long userId;
  private Long registrationId;
  private String firstName;
  private String lastName;
  private String foodSlots;
  private String privates;
  private String specials;
  private String workflowStatus;
}
