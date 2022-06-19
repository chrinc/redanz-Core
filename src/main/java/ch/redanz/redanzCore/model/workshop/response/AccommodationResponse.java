package ch.redanz.redanzCore.model.workshop.response;

import ch.redanz.redanzCore.model.workshop.SleepUtil;
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
  private List<Slot> hostSlots;
  private List<Slot> hosteeSlots;
  private List<Object> foodSlots;
  private List<SleepUtil> hostSleepUtils;
  private List<SleepUtil> hosteeSleepUtils;
}
