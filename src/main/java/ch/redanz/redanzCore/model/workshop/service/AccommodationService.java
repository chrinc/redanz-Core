package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.entities.HostRegistration;
import ch.redanz.redanzCore.model.registration.service.HostingService;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccommodationService {

  private final SlotService slotService;
  private final SleepUtilService sleepUtilService;

  public AccommodationResponse getResponse() {
    return new AccommodationResponse(
      slotService.getAllAccommodationSlots(),
      slotService.getAllAccommodationSlots(),
      slotService.getAllFoodSlots(),
      sleepUtilService.findHostSleepUtils(),
      sleepUtilService.findHosteeSleepUtils()
    );
  }
}
