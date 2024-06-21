package ch.redanz.redanzCore.model.profile.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class PersonResponse {
  private final String firstName;
  private final String lastName;
  private final String street;
  private final String postalCode;
  private final String city;
  private final Long countryId;
  private final String language;

  @JsonCreator
  public PersonResponse(
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName,
    @JsonProperty("street") String street,
    @JsonProperty("postalCode") String postalCode,
    @JsonProperty("city") String city,
    @JsonProperty("countryId") Long countryId,
    @JsonProperty("language") String language
  )

  {
    this.firstName = firstName;
    this.lastName = lastName;
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.countryId = countryId;
    this.language = language;
  }
}
