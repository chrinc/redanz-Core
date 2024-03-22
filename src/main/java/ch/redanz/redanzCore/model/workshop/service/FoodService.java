package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.FoodRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class FoodService {

  private final FoodRepo foodRepo;
  private final OutTextService outTextService;
  private final SlotService slotService;
  private final EventTypeSlotService eventTypeSlotService;
  public void save(Food food) {
    foodRepo.save(food);
  }
  public List<Food> findAll() {
    return foodRepo.findAll();
  }

  public boolean existsByName(String name) {
    return foodRepo.existsByName(name);
  }

  public Food findByName(String name) {
    return foodRepo.findByName(name);
  }

  public Food findByFoodId(Long foodId) {
    return foodRepo.findByFoodId(foodId);
  }

  public Field getField(String key) {
    Field field;
    try {
      field = Food.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateFood(JsonObject request, Event event) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Food food;

    Long foodId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    boolean foodIsNew = false;

    if (foodId == null || foodId == 0) {
      food = new Food();
      foodIsNew = true;
    } else {
      food = findByFoodId(foodId);
    }

    Long slotId;

    Food.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        log.info("type: {}", type);
        log.info("key: {}", key);
        try {
          switch(type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());
                field =  getField(key);
                field.set(food, outTextKey);
              }

              break;
            case "text":
              field = getField(key);
              field.set(food, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(food, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(food, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(food, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "date":
              field = getField(key);

              // Assuming request.get(key).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(food, request.get(key).isJsonNull() ? null : localDate);
              break;

            case "datetime":
              field = getField(key);
              // registrationStart":{"date":"2023-07-29","time":"23:00"}
              // Assuming request.get(key).getAsString() retrieves the date string
              String dateTimeDateString = request.get(key).getAsJsonObject().get("date").getAsString().substring(0, 10);
              String dateTimeTimeString = request.get(key).getAsJsonObject().get("time").isJsonNull() ? "12:00" : request.get(key).getAsJsonObject().get("time").getAsString();
              ZoneId zoneId = ZoneId.of("Europe/Zurich");
              LocalDateTime dateTime = LocalDateTime.parse(dateTimeDateString + " " + dateTimeTimeString, dateTimeFormatter);

              // hack hack hack, need fix plus Days
              ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId).plusDays(1);
              field.set(food, request.get(key).isJsonNull() ? null : zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(food, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(food);
    Slot slot = slotService.findBySlotId(request.get("slot").getAsLong());
    slotService.updateTypeSlot("food", slot, food.getFoodId());
    TypeSlot typeSlot = slotService.findByTypeObjectIdAndSlot("food", food.getFoodId(), slot);
    eventTypeSlotService.updateEventTypeSlot(typeSlot, event, 1);

  }

  public void deleteFood(JsonObject request, Event event) {
    Long foodId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Long slotId = request.get("slot").isJsonNull() ? null : request.get("slot").getAsLong();
    Food food = findByFoodId(foodId);
    TypeSlot typeSlot = slotService.findByTypeObjectIdAndSlot("food", foodId, slotService.findBySlotId(slotId));

    // delete event type slot
    eventTypeSlotService.delete((eventTypeSlotService.findByEventAndTypeSlot(event, typeSlot)));

    // delete type slot
    slotService.delete(typeSlot);

    // delete food
    foodRepo.delete(food);
  }

  public List<List<Object>> getFoodSlotsPairsByEvent(Event event) {
    List<List<Object>> foodSlots = new ArrayList<>();
    List<EventTypeSlot> eventTypeSlots;
    eventTypeSlots = slotService.findAllByEvent(event);
    eventTypeSlots.forEach(eventTypeSlot -> {
      if (eventTypeSlot.getTypeSlot().getType().equals("food")) {
        List<Object> foodSlot = new ArrayList<>();
        foodSlot.add(findByFoodId(eventTypeSlot.getTypeSlot().getTypeObjectId()));
        foodSlot.add(eventTypeSlot.getTypeSlot().getSlot());
        foodSlots.add(foodSlot);
      }
    });
    return foodSlots;
  }

  public class EventTypeSlotComparator implements Comparator<EventTypeSlot> {
    @Override
    public int compare(EventTypeSlot eventTypeSlot1, EventTypeSlot eventTypeSlot2) {
      return Integer.compare(eventTypeSlot1.getSeqNr(), eventTypeSlot2.getSeqNr());
    }
  }

  public List<Object> getFoodSlots(Event event) {
    List<Object> foodSlots = new ArrayList<>();
    List<EventTypeSlot> eventTypeSlots = new ArrayList<>();
    slotService.findAllByEvent(event).forEach(
      eventTypeSlot -> {
        if (slotService.findAllByType("food").contains(eventTypeSlot.getTypeSlot())) {
          eventTypeSlots.add(eventTypeSlot);
        }
      }
    );
    Collections.sort(eventTypeSlots, new EventTypeSlotComparator());
    eventTypeSlots.forEach(eventTypeSlot -> {
      HashMap<String, Object> foodSlot = new HashMap<>();
      foodSlot.put("food", findByFoodId(eventTypeSlot.getTypeSlot().getTypeObjectId()));
      foodSlot.put("slot", eventTypeSlot.getTypeSlot().getSlot());
      foodSlots.add(foodSlot);
    });
    return foodSlots;
  }

  public List<Map<String, String>> getFoodSchema(){
    List<Map<String, String>> foodSchema = Food.schema();
    foodSchema.forEach(item -> {
      if (item.get("key").equals("slot")) {
        item.put("list", slotService.getAllSlots().toString());
      }
    });
    return foodSchema;
  }
  public List<Map<String, String>> getFoodData(List<Event> events){
    List<Map<String, String>> foodDataList = new ArrayList<>();
    events.forEach(event -> {
      event.getEventTypeSlots().forEach(

        eventTypeSlot -> {
          if (eventTypeSlot.getTypeSlot().getType().equals("food")) {
            Food food = findByFoodId(eventTypeSlot.getTypeSlot().getTypeObjectId());
            Map<String, String> foodData = food.dataMap();
            foodData.put("eventId", Long.toString(event.getEventId()));
            foodData.put("slot", Long.toString(eventTypeSlot.getTypeSlot().getSlot().getSlotId()));
            foodDataList.add(foodData);
          }
        });
    });
    return foodDataList;
  }
}
