package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccommodationService {

  private final SlotService slotService;
  private final SleepUtilService sleepUtilService;
  private final EventService eventService;

  public AccommodationResponse getResponse() {
    return new AccommodationResponse(
      slotService.getAccommodationSlots(eventService.getCurrentEvent()),
      slotService.getAccommodationSlots(eventService.getCurrentEvent()),
      slotService.getFoodSlots(eventService.getCurrentEvent()),
      sleepUtilService.findHostSleepUtils(),
      sleepUtilService.findHosteeSleepUtils()
    );
  }
}
