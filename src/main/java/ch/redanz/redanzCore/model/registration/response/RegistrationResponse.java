package ch.redanz.redanzCore.model.registration.response;

import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RegistrationResponse {
  private Long registrationId;
  private Long userId;
  private Long eventId;
  private Long bundleId;
  private Long trackId;
  private Long danceRoleId;
  private String partnerEmail;
  private WorkflowStatus WorkflowStatus;


  public RegistrationResponse() {}

  public RegistrationResponse(Long registrationId, Long userId, Long eventId, Long bundleId) {
    this.registrationId = registrationId;
    this.userId = userId;
    this.eventId = eventId;
    this.bundleId = bundleId;
  }
}
