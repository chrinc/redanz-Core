package ch.redanz.redanzCore.model.workshop.response;

import ch.redanz.redanzCore.model.workshop.entities.EventFoodSlot;
import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AccommodationResponse {
  private List<Slot> hostSlots;
  private List<Slot> hosteeSlots;
  private Set<EventFoodSlot> foodSlots;
  private List<SleepUtil> hostSleepUtils;
  private List<SleepUtil> hosteeSleepUtils;
}
