package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.exception.HasRegistrationException;
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
  private final FoodService foodService;
  private final DiscountService discountService;
  private final EventService eventService;
  private final EventRegistrationService eventRegistrationService;

  // schema / data
  @GetMapping(path = "/data")
  public List<Map<String, String>> getStaticData() {
    return eventService.getStaticData();
  }

  @GetMapping(path = "/schema")
  public List<Map<String, String>> getSchemaStaticData() {
    return eventService.getSchemaStaticData();
  }

  @GetMapping(path = "/schema/volunteerType")
  public List<Map<String, String>> getVolunteerTypeSchema() {
    return volunteerService.getVolunteerTypeSchema();
  }

  @GetMapping(path = "/data/volunteerType")
  public List<Map<String, String>> getVolunteerTypeData() {
    return volunteerService.getVolunteerTypeData();
  }

  @GetMapping(path = "/schema/special")
  public List<Map<String, String>> getSpecialSchema() {
    return specialService.getSpecialSchema();
  }

  @GetMapping(path = "/data/special")
  public List<Map<String, String>> getSpecialData() {
    return specialService.getSpecialData();
  }

  @GetMapping(path = "/schema/private")
  public List<Map<String, String>> getPrivateSchema() {
    return privateClassService.getPrivateSchema();
  }

  @GetMapping(path = "/data/private")
  public List<Map<String, String>> getPrivateData(){
    return privateClassService.getPrivateData();
  }

  @GetMapping(path = "/schema/slot")
  public List<Map<String, String>> getSlotSchema() {
    return slotService.getSlotSchema();
  }

  @GetMapping(path = "/data/slot")
  public List<Map<String, String>> getSlotData() {
    return slotService.getSlotData();
  }

  @GetMapping(path = "/schema/food")
  public List<Map<String, String>> getFoodSchema() {
    return foodService.getFoodSchema();
  }

  @GetMapping(path = "/data/food")
  public List<Map<String, String>> getFoodData() {
    return foodService.getFoodData();
  }

  @GetMapping(path = "/schema/discount")
  public List<Map<String, String>> getDiscountSchema() {
    return discountService.getDiscountSchema();
  }

  @GetMapping(path = "/data/discount")
  public List<Map<String, String>> getDiscountData() {
    return discountService.getDiscountData();
  }


  // upsert
  @PostMapping(path = "/volunteerType/upsert")
  @Transactional
  public void upsertVolunteerType(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      volunteerService.updateVolunteerType(request);
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/special/upsert")
  @Transactional
  public void upsertSpecial(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      specialService.update(request);
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/private/upsert")
  @Transactional
  public void upsertPrivate(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      privateClassService.update(request);
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/slot/upsert")
  @Transactional
  public void upsertSlot(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      slotService.update(request);

    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/food/upsert")
  @Transactional
  public void upsertFood(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      foodService.update(request);

    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  // delete
  @PostMapping(path = "/volunteerType/delete")
  @Transactional
  public void deleteVolunteerType(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long volunteerTypeId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      VolunteerType volunteerType = volunteerService.findVolunteerTypeById(volunteerTypeId);
      if (volunteerService.isUsed(volunteerType)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        volunteerService.delete(volunteerType);
      }

    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/special/delete")
  @Transactional
  public void deleteSpecial(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long specialId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Special special = specialService.findBySpecialId(specialId);
      if (specialService.isUsed(special)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        specialService.delete(special);
      }

    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/private/delete")
  @Transactional
  public void deletePrivate(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long privateClassId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      PrivateClass privateClass = privateClassService.findByPrivateClassId(privateClassId);
      if (privateClassService.isUsed(privateClass)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        privateClassService.delete(privateClass);
      }

    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/slot/delete")
  @Transactional
  public void deleteSlot(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long slotId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Slot slot = slotService.findBySlotId(slotId);
      if (slotService.isUsed(slot)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        slotService.delete(slot);
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/food/delete")
  @Transactional
  public void deleteFood(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long foodId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Food food = foodService.findByFoodId(foodId);
      if (foodService.isUsed(food)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        foodService.delete(food);
      }

    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/discount/delete")
  @Transactional
  public void deleteDiscount(
    @RequestBody String jsonObject
  ) {
    try {
      JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
      Long id = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Discount discount = discountService.findByDiscountId(id);
      if (discountService.isUsed(discount)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        discountService.delete(discount);
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }
}
