package ch.redanz.redanzCore.model.workshop.response;

import ch.redanz.redanzCore.model.workshop.entities.EventFoodSlot;
import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
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
  private List<EventSlot> hostSlots;
  private List<EventSlot> hosteeSlots;
  private Set<EventFoodSlot> foodSlots;
  private List<SleepUtil> hostSleepUtils;
  private List<SleepUtil> hosteeSleepUtils;
}
