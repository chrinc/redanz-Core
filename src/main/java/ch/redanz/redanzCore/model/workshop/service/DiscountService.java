package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.DiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventDiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;
  private final OutTextService outTextService;
  private final EventRepo eventRepo;
  private final EventDiscountRepo eventDiscountRepo;
  public void save(Discount discount) {
    discountRepo.save(discount);
  }

  public Discount findByDiscountId(Long discountId) {
    return discountRepo.findDiscountByDiscountId(discountId);
  }

  public EventDiscount findByEventDiscountId(Long eventDiscountID) {
    return eventDiscountRepo.findByEventDiscountId(eventDiscountID);
  }
  public boolean existsByName(String name) {
    return discountRepo.existsByName(name);
  }

  public Discount findByName(String name) {
    return discountRepo.findDiscountByName(name);
  }

  public List<Discount> findAll() {
    return discountRepo.findAll();
  }


  public List<Map<String, String>> getAllDiscounts() {
    List<Map<String, String>> discounts = new ArrayList<>();

    findAll().forEach(discount -> {
      discounts.add(discount.dataMap());
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

  public void update(JsonObject request) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Discount discount;

    Long discountId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (discountId == null || discountId == 0) {
      discount = new Discount();
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

                if (outTextKey != null) {
                  field = getField(key);
                  field.set(discount, outTextKey);
                }
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

              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(discount, request.get(key).isJsonNull() ? null : localDate);
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
  }

  public void deleteDiscount(JsonObject request, Event event) {
    Long discountId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Discount discount = findByDiscountId(discountId);
    delete(discount);
  }

  public void delete(Discount discount) {
    outTextService.delete(discount.getDescription());
    outTextService.delete(discount.getName());
    discountRepo.delete(discount);
  }

//  public void delete(TrackDiscount trackDiscount) {
//    trackDiscountRepo.delete(trackDiscount);
//  }

  public List<Map<String, String>> getEventDiscountsMap(Event event) {
    List<Map<String, String>> eventDiscounts = new ArrayList<>();
    event.getEventDiscounts().forEach(eventDiscount -> {
      Map<String, String> eventDiscountData = eventDiscount.dataMap();
      eventDiscounts.add(eventDiscountData);
    });

    return eventDiscounts;
  }

  public List<Map<String, String>> getDiscountsMap(Event event) {
    List<Map<String, String>> discounts = new ArrayList<>();
    event.getEventDiscounts().forEach(eventDiscount -> {
      Map<String, String> discount = eventDiscount.dataMap();
      discount.put("discount", eventDiscount.getDiscount().dataMap().toString());
      discount.put("name", eventDiscount.getDiscount().getName());
      discounts.add(discount);
    });
    return discounts;
  }

  public List<Map<String, String>> getDiscountsMap() {
    return discountRepo.findAll().stream()
      .map(Discount::dataMap)
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getDiscountSchema(){
    return Discount.schema();
  }
  public List<Map<String, String>> getDiscountData(){
    List<Map<String, String>> discountsData = new ArrayList<>();
    discountRepo.findAll().forEach(discount -> {
      Map<String, String> discountData = discount.dataMap();
      discountsData.add(discountData);
    });

    return discountsData;
  }
  public boolean isUsed(Discount discount) {
    return eventDiscountRepo.existsByDiscount(discount);
  }
}
