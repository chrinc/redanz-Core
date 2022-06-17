package ch.redanz.redanzCore.model.workshop.response;

import ch.redanz.redanzCore.model.workshop.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AccommodationResponse {
  private List<Slot> slots;
  private List<Object> foodSlots;
}
