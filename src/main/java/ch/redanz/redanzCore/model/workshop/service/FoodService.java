package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.config.SlotType;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventFoodSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class FoodService {

  private final OutTextService outTextService;
  private final SlotService slotService;
  private final EventFoodSlotRepo eventFoodSlotRepo;


//  public void save(Food food) {
//    foodRepo.save(food);
//  }

//  public List<Food> findAll() {
//    return foodRepo.findAll();
//  }

//  public boolean existsByName(String name) {
//    return foodRepo.existsByName(name);
//  }

  public EventFoodSlot findByName(String name) {
    return eventFoodSlotRepo.findByName(name);
  }

//  public Food findByFoodId(Long foodId) {
//    return foodRepo.findByFoodId(foodId);
//  }
  public EventFoodSlot findEventFoodSlotById(Long eventFoodSlotId) {
    return eventFoodSlotRepo.findById(eventFoodSlotId).orElse(null);
  }
//  public Field getField(String key) {
//    Field field;
//    try {
//      field = Food.class.getDeclaredField(key);
//    } catch (NoSuchFieldException e) {
//      throw new RuntimeException(e);
//    }
//    field.setAccessible(true);
//    return field;
//  }
//
//  public void update(JsonObject request) throws IOException, TemplateException {
//    Long foodId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
//    Food food;
//
//    if (foodId == null || foodId == 0) {
//      food = new Food();
//    } else {
//      food = findByFoodId(foodId);
//    }
//
//    Food.schema().forEach(
//      stringStringMap -> {
//        String key = stringStringMap.get("key");
//        String type = stringStringMap.get("type");
//        Field field;
//
//        try {
//          switch(type) {
//            case "label":
//              if (request.get("label") != null && request.get("label").isJsonArray()) {
//                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());
//                field =  getField(key);
//                field.set(food, outTextKey);
//              }
//
//              break;
//            case "text":
//              field = getField(key);
//              field.set(food, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
//              break;
//
//            case "number":
//              field = getField(key);
//              field.set(food, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
//              break;
//
//            case "double":
//              field = getField(key);
//              field.set(food, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
//              break;
//
//            case "color":
//              field = getField(key);
//              field.set(food, request.get(key).isJsonNull() ? null :
//                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
//                  request.get(key).getAsJsonObject().get("hex").getAsString());
//              break;
//
//            case "bool":
//              field = getField(key);
//              field.set(food, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
//              break;
//
//            default:
//              // Nothing will happen here
//          }
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    );
//    save(food);
//  }
//
//
//  public void delete(Food food, Slot slot, Event event) {
//    TypeSlot typeSlot = slotService.findByTypeObjectIdAndSlot("food", food.getFoodId(), slot);
//
//    // delete event type slot
//    eventTypeSlotService.delete((eventTypeSlotService.findByEventAndTypeSlot(event, typeSlot)));
//
//    // delete type slot
//    slotService.delete(typeSlot);
//
//    // delete food
//    delete(food);
//  }
//
//  public void delete(Food food) {
//    outTextService.delete(food.getName());
//    outTextService.delete(food.getDescription());
//    foodRepo.delete(food);
//  }

  public List<EventFoodSlot> getEventFoodSlots(Event event) {
    return eventFoodSlotRepo.findByEvent(event);
  }

  public class EventTypeSlotComparator implements Comparator<EventSlot> {
    @Override
    public int compare(EventSlot eventSlot1, EventSlot eventSlot2) {
      return Integer.compare(eventSlot1.getSeqNr(), eventSlot2.getSeqNr());
    }
  }

  public Set<EventFoodSlot> getFoodSlots(Event event) {
    return event.getEventFoodSlots();
  }

  public List<Map<String, String>> getFoodSlotSchema(Event event) {
    List<Map<String, String>> foodSchema = EventFoodSlot.schema();
    foodSchema.forEach(item -> {
      switch (item.get("key")) {
        case "eventSlot":
          item.put("list", slotService.eventSlotData(event, SlotType.FOOD).toString());
          break;
      }
    });
    return foodSchema;
  }

  public List<Map<String, String>> getFoodData(Event event) {
    List<Map<String, String>> foodDataList = new ArrayList<>();

    event.getEventFoodSlots().forEach(
      eventFoodSlot -> {
        Map<String, String> eventFoodData = eventFoodSlot.dataMap();
        eventFoodData.put("eventId", Long.toString(event.getEventId()));
        eventFoodData.put("eventSlot", Long.toString(eventFoodSlot.getEventSlot().getEventSlotId()));
        eventFoodData.put("name", eventFoodSlot.getName());
        eventFoodData.put("description", eventFoodSlot.getDescription());
        eventFoodData.put("id", Long.toString(eventFoodSlot.getEventFoodSlotId()));
        eventFoodData.put("price", Double.toString(eventFoodSlot.getPrice()));
        eventFoodData.put("seqNr", Integer.toString(eventFoodSlot.getSeqNr()));
        foodDataList.add(eventFoodData);
      });
    return foodDataList;
  }

//  public List<Map<String, String>> getFoodData() {
//    List<Map<String, String>> foodDataList = new ArrayList<>();
//    foodRepo.findAll().forEach(food -> {
//      // discount data
//      Map<String, String> foodData = food.dataMap();
//      foodDataList.add(foodData);
//    });
//    return foodDataList;
//  }
//
//  public List<Map<String, String>> getFoodSchema() {
//    return Food.schema();
//  }
//
//  public Boolean isUsed(Food food) {
//    AtomicBoolean isUsed = new AtomicBoolean(false);
//    return eventFoodSlotRepo.existsByFood(food);
//  }
}
