package ch.redanz.redanzCore.model.registration.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class RegistrationRequest {
  private final Long eventId;
  private final Long bundleId;
  private final Long trackId;
  private final Long danceRoleId;
  private final String partnerEmail;
//  private final List<Object> foodRegistration;
//  private final List<Object> hostRegistration;

}

