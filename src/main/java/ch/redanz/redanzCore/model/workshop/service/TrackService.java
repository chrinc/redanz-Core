package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.TrackBundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackDanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackDiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TrackService {

  private final TrackRepo trackRepo;
  private final TrackBundleRepo trackBundleRepo;
  private final TrackDanceRoleRepo trackDanceRoleRepo;
  private final TrackDiscountRepo trackDiscountRepo;
  private final DiscountService discountService;
  private final OutTextService outTextService;
  public boolean trackDiscountExists(Track track, Discount discount) {
    return trackDiscountRepo.existsByTrackAndDiscount(track, discount);
  }
  public void save(Track track) {
    trackRepo.save(track);
  }

  public void save(TrackDanceRole trackDanceRole) {
    trackDanceRoleRepo.save(trackDanceRole);
  }

  public void save(TrackDiscount trackDiscount) {
    trackDiscountRepo.save(trackDiscount);
  }

  public boolean existsByTrackDanceRole(Track track, DanceRole danceRole) {
    return trackDanceRoleRepo.existsByTrackAndDanceRole(track, danceRole);
  }
  public List<Track> getAll(){
    return trackRepo.findAll();
  }

  public boolean bundleHasTrack(Bundle bundle) {
    return !trackBundleRepo.findAllByBundle(bundle).isEmpty();
  }

  public boolean existsByName(String TrackName) {
    return trackRepo.existsByName(TrackName);
  }

  public Track findByTrackId(Long trackId) {
    return trackRepo.findByTrackId(trackId);
  }

  public Track findByName(String name) {
    return trackRepo.findByName(name);
  }


  public Field getField(String key) {
    Field field;
    try {
      field = Track.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateTrack(JsonObject request, Bundle bundle) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Track track;
    List<Discount> newDiscounts = new ArrayList<>();
    Long trackId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    boolean trackIsNew = false;

    if (trackId == null || trackId == 0) {
      track = new Track();
      trackIsNew = true;
    } else {
      track = findByTrackId(trackId);
    }

    Track.schema().forEach(
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
                field.set(track, outTextKey);
              }
              break;
            case "text":
              field = getField(key);
              field.set(track, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(track, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(track, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;


            case "color":
              field = getField(key);
              field.set(track, request.get(key).isJsonNull() ? null :
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

              field.set(track, request.get(key).isJsonNull() ? null : localDate);
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
              field.set(track, request.get(key).isJsonNull() ? null : zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(track, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "multiselect":
              log.info("key, {}", key);

              if (request.get(key) != null && request.get(key).isJsonArray()) {
                request.get(key).getAsJsonArray().forEach(item -> {
                  newDiscounts.add(discountService.findByDiscountId(item.getAsLong()));
                });
              }
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(track);

    if (trackIsNew) {
      trackBundleRepo.save(
        new BundleTrack(
          track, bundle
        )
      );
    };

    List<Discount> existingDiscounts = trackDiscountRepo.findAllByTrack(track)
      .stream()
      .map(TrackDiscount::getDiscount)
      .collect(Collectors.toList());

    Set<Discount> newDiscountsSet = new HashSet<>(newDiscounts);

    // Save new discounts
    newDiscounts.stream()
      .filter(discount -> !existingDiscounts.contains(discount))
      .forEach(discount -> save(new TrackDiscount(discount, track)));

    // Remove existing discounts not in new discounts
    existingDiscounts.stream()
      .filter(existingDiscount -> !newDiscountsSet.contains(existingDiscount))
      .forEach(existingDiscount -> {
        TrackDiscountId trackDiscountId = trackDiscountRepo.findByDiscountAndTrack(existingDiscount, track).getTrackDiscountId();
        trackDiscountRepo.deleteById(trackDiscountId);
      });
  }

  public void deleteTrack(JsonObject request) {
    Long trackId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Track track = findByTrackId(trackId);
    trackRepo.delete(track);
  }


  public List<Long> trackDiscountIdList(Track track){
    List<Long> trackIdList = new ArrayList<>();
    trackDiscountRepo.findAllByTrack(track).forEach(trackDiscount -> {
      trackIdList.add(trackDiscount.getDiscount().getDiscountId());
    });

    return trackIdList;
  }


  public List<Map<String, String>> getTrackSchema(Event event){
    List<Map<String, String>> trackSchema = Track.schema();
    trackSchema.forEach(item -> {
      if (item.get("key").equals("discounts")) {
        item.put("list", discountService.getDiscounts(event).toString());
      }
    });
    return trackSchema;
  }
  public List<Map<String, String>> getTracksData(List<Bundle> bundles){
    List<Map<String, String>> tracksData = new ArrayList<>();
    bundles.forEach(bundle -> {
      bundle.getBundleTracks().forEach(
        bundleTrack -> {
          Map<String, String> trackData = bundleTrack.getTrack().dataMap();
          trackData.put("bundleId", Long.toString(bundle.getBundleId()));
          trackData.put("discounts", trackDiscountIdList(bundleTrack.getTrack()).toString());
          tracksData.add(trackData);
        });
    });
    return tracksData;
  }

}
