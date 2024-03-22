package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.DiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventDiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackDiscountRepo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;
  private final EventDiscountRepo eventDiscountRepo;
  private final TrackDiscountRepo trackDiscountRepo;
  private final OutTextService outTextService;
  public void save(Discount discount) {
    discountRepo.save(discount);
  }
  public void save(EventDiscount eventDiscount) {
    eventDiscountRepo.save(eventDiscount);
  }
  public Discount findByDiscountId(Long discountId) {
    return discountRepo.findDiscountByDiscountId(discountId);
  }
  public boolean existsByName(String name) {
    return discountRepo.existsByName(name);
  }
  public boolean eventDiscountExists(Event event, Discount discount) {
    return eventDiscountRepo.existsByEventAndDiscount(event, discount);
  }
  public Discount findByName(String name) {
    return discountRepo.findDiscountByName(name);
  }

  public List<Discount> findAll() {
    return discountRepo.findAll();
  }
  public List<Discount> findAllByEvent(Event event) {
    List<Discount> discounts = new ArrayList<>();
    eventDiscountRepo.findAllByEvent(event).forEach(eventDiscount -> {
      discounts.add(eventDiscount.getDiscount());
    });
    return discounts;
  }

  public Field getField(String key) {
    Field field;
    try {
      field = Discount.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateDiscount(JsonObject request, Event event) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Discount discount;
    boolean discountIsNew = false;

    Long discountId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (discountId == null || discountId == 0) {
      discount = new Discount();
      discountIsNew = true;
    } else {
      discount = findByDiscountId(discountId);
    }

    Discount.schema().forEach(
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
                field.set(discount, outTextKey);
              }
              break;
            case "text":
              field = getField(key);

              field.set(discount, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(discount, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(discount, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(discount, request.get(key).isJsonNull() ? null :
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

              field.set(discount, request.get(key).isJsonNull() ? null : localDate);
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
              field.set(discount, request.get(key).isJsonNull() ? null : zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(discount, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(discount);

    if (discountIsNew) {
      eventDiscountRepo.save(
        new EventDiscount(
          discount,
          event
        )
      );
    }
  }

  public void deleteDiscount(JsonObject request, Event event) {
    Long discountId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Discount discount = findByDiscountId(discountId);
    eventDiscountRepo.deleteById(eventDiscountRepo.findByEventAndDiscount(event, discount).getEventDiscountId());
    discountRepo.delete(discount);
  }

  public List<Map<String, String>> getDiscounts(Event event) {
    List<Map<String, String>> discounts = new ArrayList<>();

    eventDiscountRepo.findAllByEvent(event).forEach(eventDiscount -> {
      Map<String, String> discountData = eventDiscount.getDiscount().dataMap();
      discounts.add(discountData);
    });

    return discounts;
  }

  public List<Map<String, String>> getDiscountSchema(){
    return Discount.schema();
  }
  public List<Map<String, String>> getDiscountData(List<Event> eventList){
    log.info("inc get event discounts start");
    List<Map<String, String>> discountsData = new ArrayList<>();
    eventList.forEach(event -> {
      event.getEventDiscounts().forEach(eventDiscount -> {
        // discount data
        Map<String, String> discountData = eventDiscount.getDiscount().dataMap();

        // add event info
        discountData.put("eventId", Long.toString(event.getEventId()));

        discountsData.add(discountData);
      });
    });

    log.info("inc return data");
    return discountsData;
  }

}
