package ch.redanz.redanzCore.model.profile.response;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class UserResponse {
  private final String username;
  private final String password;
}
