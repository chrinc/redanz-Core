package ch.redanz.redanzCore.model.profile.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class UserResponse {
  private final String username;
  private final String password;

  @JsonCreator
  public UserResponse(
    @JsonProperty("username") String username,
    @JsonProperty("password") String password) {
    this.username = username;
    this.password = password;
  }
}
