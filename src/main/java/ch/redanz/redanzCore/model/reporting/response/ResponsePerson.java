package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponsePerson {
  private String firstName;
  private String LastName;
  private String address;
  private String postalCode;
  private String city;
  private String email;
  private String role;
  private String lang;
  private Long personId;
}
