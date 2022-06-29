package ch.redanz.redanzCore.model.registration.response;

import ch.redanz.redanzCore.model.registration.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.FoodRegistration;
import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import lombok.*;

import java.util.List;
import java.util.Map;

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
  private List<FoodRegistration> foodRegistrations;
  private Map<String, List<Object>> hostRegistration;
  private Map<String, List<Object>> hosteeRegistration;
  private Map<String, List<Object>> volunteerRegistration;
  private Map<String, String> scholarshipRegistration;
  private Map<String, String> donationRegistration;
  private List<DiscountRegistration> discountRegistrations;


  public RegistrationResponse() {}

  public RegistrationResponse(Long registrationId, Long userId, Long eventId, Long bundleId) {
    this.registrationId = registrationId;
    this.userId = userId;
    this.eventId = eventId;
    this.bundleId = bundleId;
  }
}
