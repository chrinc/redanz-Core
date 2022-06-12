package ch.redanz.redanzCore.model.profile.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class UserResponse {
  private final String email;
  private final String password;
}
