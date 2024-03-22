package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.BundleEventRepo;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackBundleRepo;
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

@Service
@AllArgsConstructor
@Slf4j
public class BundleService {
//    private final EventService eventService;
    private final BundleRepo bundleRepo;
    private final BundleEventRepo bundleEventRepo;
    private final TrackBundleRepo trackBundleRepo;
    private final OutTextService outTextService;
    private final TrackService trackService;

    public List<Bundle> getAll(){
        return bundleRepo.findAll();
    }
    public void save(Bundle bundle) {
        bundleRepo.save(bundle);
    }
    public void save(BundleTrack bundleTrack) {
        trackBundleRepo.save(bundleTrack);
    }

    public List<Bundle> getAllByEvent(Event event) {
        List<EventBundle> eventBundles;
        List<Bundle> bundles = new ArrayList<>();
        eventBundles = bundleEventRepo.findAllByEvent(event);
        eventBundles.forEach(eventBundle -> {
            bundles.add(eventBundle.getBundle());
        });

        return bundles;
    }
    public boolean existsByName(String name) {
        return bundleRepo.existsByName(name);
    }
    public Bundle findByBundleId(Long bundleId) {
        return bundleRepo.findByBundleId(bundleId);
    }
    public BundleTrack findByBundleAndTrack(Bundle bundle, Track track) {
        return trackBundleRepo.findByBundleAndTrack(bundle, track);
    }
    public void delete(Bundle bundle) {
        bundleRepo.delete(bundle);
    }
    public Bundle findByName(String name){return bundleRepo.findByName(name);}

    public boolean bundleTrackExists(Bundle bundle, Track track) {
        return trackBundleRepo.existsByBundleAndTrack(bundle, track);
    }

    public Field getField(String key) {
        Field field;
        try {
            field = Bundle.class.getDeclaredField(key);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        return field;
    }

    public void updateBundle(JsonObject request, Event event) throws IOException, TemplateException {
        log.info("inc@updateBundle, request: {}", request);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Bundle bundle;
        boolean isNew = false;

        Long bundleId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

        if (bundleId == null || bundleId == 0) {
            bundle = new Bundle();
            isNew = true;
        } else {
            bundle = findByBundleId(bundleId);
        }

        Bundle.schema().forEach(
          stringStringMap -> {
              String key = stringStringMap.get("key");
              String type = stringStringMap.get("type");
              Field field;
              log.info("key, {}", key);
              log.info("type, {}", type);

              try {
                  switch(type) {
                      case "label":
                          if (request.get("label") != null && request.get("label").isJsonArray()) {
                              String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());
                              field =  getField(key);
                              field.set(bundle, outTextKey);
                          }
                          break;
                      case "text":
                          field = getField(key);

                          field.set(bundle, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
                          break;

                      case "number":
                          field = getField(key);
                          field.set(bundle, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
                          break;

                      case "double":
                          field = getField(key);
                          field.set(bundle, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
                          break;

                      case "color":
                          field = getField(key);
                          log.info("isjsonnull, {}", request.get(key).isJsonNull());
                          log.info("is json object?, {}", request.get(key).isJsonObject());
                          field.set(bundle, request.get(key).isJsonNull() ? null :
                            request.get(key).isJsonObject() ? request.get(key).getAsJsonObject().get("hex").getAsString()
                              : request.get(key).getAsString())
                            ;
                          break;

                      case "date":
                          field = getField(key);

                          // Assuming request.get(key).getAsString() retrieves the date string
                          String dateString = request.get(key).getAsString();

                          // Parse the string into a LocalDate object
                          // hack hack hack, need fix
                          LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

                          field.set(bundle, request.get(key).isJsonNull() ? null : localDate);
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
                          field.set(bundle, request.get(key).isJsonNull() ? null : zonedDateTime);
                          break;

                      case "bool":
                          field = getField(key);
                          field.set(bundle, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
                          break;

                      default:
                          // Nothing will happen here
                  }
              } catch (IllegalAccessException e) {
                  throw new RuntimeException(e);
              }
          }
        );
        log.info("bfr save");
        save(bundle);

        log.info("bfr isNew, {}", isNew);
        if (isNew) {
            bundleEventRepo.save(
              new EventBundle(
                bundle,
                event
              )
            );
        }
    }

    public void deleteBundle(JsonObject request) {
        Long bundleId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
        Bundle bundle = findByBundleId(bundleId);
        bundleRepo.delete(bundle);
    }

    public void deleteBundleTrack(JsonObject request) {
        Long trackId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
        Track track = trackService.findByTrackId(trackId);
        Bundle bundle = findByBundleId(request.get("bundleId").getAsLong());
        BundleTrack bundleTrack = findByBundleAndTrack(bundle, track);
        trackBundleRepo.delete(bundleTrack);
    }
}
