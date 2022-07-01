package ch.redanz.redanzCore.model.registration.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
}

