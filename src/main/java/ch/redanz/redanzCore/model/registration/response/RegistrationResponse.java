package ch.redanz.redanzCore.model.registration.response;

import ch.redanz.redanzCore.model.registration.entities.*;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
  private Long registrationId;
  private Long userId;
  private Event event;
  private Long bundleId;
  private Long trackId;
  private Long danceRoleId;
  private String partnerEmail;
  private WorkflowStatus workflowStatus;
  private List<FoodRegistration> foodRegistrations;
  private Map<String, List<Object>> hostRegistration;
  private Map<String, List<Object>> hosteeRegistration;
  private Map<String, List<Object>> volunteerRegistration;
  private Map<String, String> scholarshipRegistration;
  private Map<String, String> donationRegistration;
  private List<DiscountRegistration> discountRegistrations;
  private List<SpecialRegistration> specialRegistrations;
  private List<PrivateClassRegistration> privateClassRegistrations;

  public RegistrationResponse(Long registrationId, Long userId, Event event, Long bundleId) {
    this.registrationId = registrationId;
    this.userId = userId;
    this.event = event;
    this.bundleId = bundleId;
  }
  public RegistrationResponse(Long userId, Event event) {
    this.userId = userId;
    this.event = event;
//    this.workflowStatus = workflowStatus;
  }
}
