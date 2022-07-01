package ch.redanz.redanzCore.model.registration.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FoodRegistrationRequest {
  private List<Map<String, Map<String, String>>> foodSlot;
}
