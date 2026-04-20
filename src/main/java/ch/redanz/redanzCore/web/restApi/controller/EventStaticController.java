package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.exception.HasRegistrationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("core-api/app/event/static")
@AllArgsConstructor
public class EventStaticController {
  private final SlotService slotService;
  private final SpecialService specialService;
  private final PrivateClassService privateClassService;
  private final VolunteerService volunteerService;
  private final DiscountService discountService;
  private final EventService eventService;
  private final BaseParService baseParService;

  // schema / data
  @GetMapping(path = "/data")
  public List<Map<String, String>> getStaticData() {
    return eventService.getStaticData();
  }

  @GetMapping(path = "/schema")
  public List<Map<String, String>> getSchemaStaticData() {
    return eventService.getSchemaStaticData();
  }

  @GetMapping(path = "/schema/eventSpecial")
  public List<Map<String, String>> getEventSpecialSchema() {
    return specialService.getEventSpecialSchema();
  }

  @GetMapping(path = "/data/eventSpecial")
  public List<Map<String, String>> getEventSpecialData() {
    return specialService.getEventSpecialData();
  }

  @GetMapping(path = "/base-par/data")
  public List<Map<String, Object>> baseParData(
    Long eventId
  ) {
    return baseParService.getData(eventService.findByEventId(eventId));
  }

  @PostMapping(path = "/base-par/upsert")
  public void upsertBaseParData(
    @RequestBody String jsonString
  ) {
    try {
      JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
      baseParService.upsert(jsonArray);
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }


}
