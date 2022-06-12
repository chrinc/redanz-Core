package ch.redanz.redanzCore.model.profile.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class PersonResponse {
    private final String firstName;
    private final String lastName;
    private final String street;
    private final String streetNumber;
    private final String postalCode;
    private final String city;
    private final Long countryId;
}
