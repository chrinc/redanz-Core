package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseEmailLogs {
  private Long userId;
  private Long registrationId;
  private String firstName;
  private String lastName;
  private String email;
  private String bundle;
  private String workflowStatus;
  private Long amountDue;
  private Long totalAmount;
  private String dateReceived;
  private String dateReleased;
  private String dateDone;
  private String dateReminderSent;
  private String dateCancelled;
}
