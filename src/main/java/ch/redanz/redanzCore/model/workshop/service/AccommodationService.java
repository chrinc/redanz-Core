package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccommodationService {

  SlotService slotService;
  FoodService foodService;
  SleepUtilService sleepUtilService;

  public AccommodationResponse getAll() {
    return new AccommodationResponse(
      slotService.getAllAccommodationSlots(),
      slotService.getAllAccommodationSlots(),
      slotService.getAllFoodSlots(),
      sleepUtilService.findHostSleepUtils(),
      sleepUtilService.findHosteeSleepUtils()
    );
  }
}
