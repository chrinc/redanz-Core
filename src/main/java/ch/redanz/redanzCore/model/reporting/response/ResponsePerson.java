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
  private String street;
  private String postalCode;
  private String city;
  private String email;
  private String role;
  private String language;
  private Long personId;
  private Long countryId;
  private String mobile;
}
