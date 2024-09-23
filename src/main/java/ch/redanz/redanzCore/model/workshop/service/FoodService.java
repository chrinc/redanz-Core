package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventFoodSlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.FoodRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@AllArgsConstructor
public class FoodService {

  private final FoodRepo foodRepo;
  private final OutTextService outTextService;
  private final SlotService slotService;
  private final EventTypeSlotService eventTypeSlotService;
  private final EventFoodSlotRepo eventFoodSlotRepo;
  private final EventRepo eventRepo;


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

  public void update(JsonObject request) throws IOException, TemplateException {
    Long foodId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Food food;

    if (foodId == null || foodId == 0) {
      food = new Food();
    } else {
      food = findByFoodId(foodId);
    }

    Food.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

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
  }


  public void delete(Food food, Slot slot, Event event) {
    TypeSlot typeSlot = slotService.findByTypeObjectIdAndSlot("food", food.getFoodId(), slot);

    // delete event type slot
    eventTypeSlotService.delete((eventTypeSlotService.findByEventAndTypeSlot(event, typeSlot)));

    // delete type slot
    slotService.delete(typeSlot);

    // delete food
    delete(food);
  }

  public void delete(Food food) {
    outTextService.delete(food.getName());
    outTextService.delete(food.getDescription());
    foodRepo.delete(food);
  }

  public List<EventFoodSlot> getEventFoodSlots(Event event) {
    return eventFoodSlotRepo.findByEvent(event);
  }

  public class EventTypeSlotComparator implements Comparator<EventTypeSlot> {
    @Override
    public int compare(EventTypeSlot eventTypeSlot1, EventTypeSlot eventTypeSlot2) {
      return Integer.compare(eventTypeSlot1.getSeqNr(), eventTypeSlot2.getSeqNr());
    }
  }

  public Set<EventFoodSlot> getFoodSlots(Event event) {
    return event.getEventFoodSlots();
  }

  public List<Map<String, String>> getFoodSlotSchema() {
    List<Map<String, String>> foodSchema = EventFoodSlot.schema();
    foodSchema.forEach(item -> {
      switch (item.get("key")) {
        case "slot":
          item.put("list", slotService.getAllSlots().toString());
          break;
        case "food":
          item.put("list", getFoodData().toString());
          break;
      }
    });
    return foodSchema;
  }

  public List<Map<String, String>> getFoodData(Event event) {
    List<Map<String, String>> foodDataList = new ArrayList<>();

    event.getEventFoodSlots().forEach(
      eventFoodSlot -> {
          Food food = eventFoodSlot.getFood();
          Map<String, String> foodData = food.dataMap();
          foodData.put("eventId", Long.toString(event.getEventId()));
          foodData.put("slot", Long.toString(eventFoodSlot.getSlot().getSlotId()));
          foodData.put("food", Long.toString(eventFoodSlot.getFood().getFoodId()));
          foodData.put("id", Long.toString(eventFoodSlot.getEventFoodSlotId()));
          foodData.put("price", Double.toString(eventFoodSlot.getPrice()));
          foodData.put("seqNr", Integer.toString(eventFoodSlot.getSeqNr()));
          foodDataList.add(foodData);
      });
    return foodDataList;
  }

  public List<Map<String, String>> getFoodData() {
    List<Map<String, String>> foodDataList = new ArrayList<>();
    foodRepo.findAll().forEach(food -> {
      // discount data
      Map<String, String> foodData = food.dataMap();
      foodDataList.add(foodData);
    });
    return foodDataList;
  }

  public List<Map<String, String>> getFoodSchema() {
    return Food.schema();
  }


//  public List<EventTypeSlot> eventFoodList(Event event) {
//    return eventTypeSlotService.findByEventAndType(event, "food");
//  }
  public boolean isUsed(Food food) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      event.getEventTypeSlots().forEach(eventTypeSlot -> {
        if (eventTypeSlot.getTypeSlot().getType().equals("food")
          && eventTypeSlot.getTypeSlot().getTypeObjectId().equals(food.getFoodId())
        ) {
          isUsed.set(true);
        }
      });
    });
    return isUsed.get();
  }
}
