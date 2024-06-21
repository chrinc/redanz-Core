package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.*;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@AllArgsConstructor
@Slf4j
public class BundleService {
//    private final EventService eventService;
    private final BundleRepo bundleRepo;
    private final EventBundleRepo eventBundleRepo;
    private final OutTextService outTextService;
    private final TrackService trackService;
    private final SlotService slotService;
    private final EventSpecialRepo eventSpecialRepo;
    private final EventTrackRepo eventTrackRepo;
    private final EventRepo eventRepo;
//    private final BundleSpecialRepo bundleSpecialRepo;

    public List<Bundle> getAll(){
        return bundleRepo.findAll();
    }
    public void save(Bundle bundle) {
        bundleRepo.save(bundle);
    }
//    public void save(BundleSpecial bundleSpecial) {
//        bundleSpecialRepo.save(bundleSpecial);
//    }

    public List<Bundle> getAllByEvent(Event event) {
        List<EventBundle> eventBundles;
        List<Bundle> bundles = new ArrayList<>();
        eventBundles = eventBundleRepo.findAllByEvent(event);
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

    public void delete(Bundle bundle, Track track) {
        Set<EventTrack> filteredEventTracks = bundle.getEventTracks().stream()
          .filter(eventTrack -> !eventTrack.getTrack().equals(track))
          .collect(Collectors.toSet());
        bundle.setEventTracks(filteredEventTracks);
        save(bundle);
    }

    public void delete(Bundle bundle) {
        bundleRepo.delete(bundle);
    }



    public Bundle findByName(String name){return bundleRepo.findByName(name);}
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Bundle bundle;
        boolean isNew = false;
        Set<Slot> newPartySlots = new HashSet<>();
        Set<EventSpecial> newEventSpecials = new HashSet<>();
        Set<EventTrack> newEventTracks = new HashSet<>();

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
              try {
                 // log.info("key, {}", key);
                 // log.info("type, {}", type);
                  switch(type) {
                      case "label":
                          if (request.get("label") != null && request.get("label").isJsonArray()) {
                              String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                              if (outTextKey != null) {
                                  field = getField(key);
                                  field.set(bundle, outTextKey);
                              }
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
//                          log.info("isjsonnull, {}", request.get(key).isJsonNull());
//                          log.info("is json object?, {}", request.get(key).isJsonObject());
                          field.set(bundle, request.get(key).isJsonNull() ? null :
                            request.get(key).isJsonObject() ? request.get(key).getAsJsonObject().get("hex").getAsString()
                              : request.get(key).getAsString())
                            ;
                          break;

                      case "date":
                          field = getField(key);

                          // Assuming request.get(eventPartKey).getAsString() retrieves the date string
                          String dateString = request.get(key).getAsString();

                          // Parse the string into a LocalDate object
                          // hack hack hack, need fix
                          LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

                          field.set(bundle, request.get(key).isJsonNull() ? null : localDate);
                          break;

                      case "datetime":
                          field = getField(key);
                          // registrationStart":{"date":"2023-07-29","time":"23:00"}
                          // Assuming request.get(eventPartKey).getAsString() retrieves the date string
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

                      case "multiselect":
                          if (request.get(key) != null && request.get(key).isJsonArray()) {
                              request.get(key).getAsJsonArray().forEach(item -> {
                                  newPartySlots.add(slotService.findBySlotId(item.getAsLong()));
                              });
                          }
                          break;

                      case "multiselectText":
                          // tracks
                          if (request.get(key) != null && request.get(key).isJsonArray()) {
                              request.get(key).getAsJsonArray().forEach(item -> {
                                  newEventTracks.add(eventTrackRepo.findByEventTrackId(item.getAsLong()));
                              });
                          }
                          break;
                      case "multiselectInfo":
//                          log.info("eventPartKey, {}", key);
//                          log.info("eventPartKey, {}", request.get(key));
                          if (request.get(key) != null && request.get(key).isJsonArray()) {
                              if (key.equals("bundleSpecial")) {
                                  request.get(key).getAsJsonArray().forEach(item -> {
                                      newEventSpecials.add(eventSpecialRepo.findByEventSpecialId(item.getAsLong()));
                                  });
                              }
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
        bundle.setPartySlots(newPartySlots);
        bundle.setEventSpecials(newEventSpecials);
        bundle.setEventTracks(newEventTracks);
        save(bundle);

        if (isNew) {
            eventBundleRepo.save(
              new EventBundle(
                bundle,
                event
              )
            );
        }
    }


    @Transactional
    public void deleteBundle(Bundle bundle) {
        outTextService.delete(bundle.getDescription());
        bundle.setEventTracks(new HashSet<>());
        bundle.setPartySlots(new HashSet<>());
        bundle.setEventSpecials(new HashSet<>());
        bundleRepo.delete(bundle);
    }


    public Bundle clone(Event event, Bundle baseBundle, Set<EventTrack> eventTracks) {

        String description = outTextService.clone(baseBundle.getDescription());
        Set<Slot> newPartySlots = new HashSet<>(baseBundle.getPartySlots());
        Bundle newBundle = new Bundle(
          baseBundle.getName(),
          baseBundle.getPrice(),
          description,
          baseBundle.getCapacity(),
          baseBundle.getSimpleTicket(),
          baseBundle.getSeqNr(),
          newPartySlots,
          baseBundle.getColor(),
          baseBundle.getActive()
        );

        // Clone Event Specials
        Set<EventSpecial> newEventSpecials = new HashSet<>(event.getEventSpecials());
        Set<Special> oldSpecials = baseBundle.getEventSpecials().stream()
          .map(EventSpecial::getSpecial)
          .collect(Collectors.toSet());

        newEventSpecials.stream()
          .filter(eventSpecial -> oldSpecials.contains(eventSpecial.getSpecial()))
          .collect(Collectors.toSet());
        newBundle.setEventSpecials(newEventSpecials);

        // Clone Tracks
        if (eventTracks == null) {
            Set<EventTrack> newEventTracks = new HashSet<>(baseBundle.getEventTracks());
            Set<Track> oldTracks = baseBundle.getEventTracks().stream()
              .map(EventTrack::getTrack)
              .collect(Collectors.toSet());

            newEventTracks.stream()
              .filter(eventTrack -> oldTracks.contains(eventTrack.getTrack()))
              .collect(Collectors.toSet());
            newBundle.setEventTracks(newEventTracks);
        } else {
            newBundle.setEventTracks(eventTracks);
        }
        save(newBundle);

        return newBundle;
    }
    public List<Long> partySlotIds(Bundle bundle) {
        return bundle.getPartySlots().stream()
          .map(Slot::getSlotId)
          .collect(Collectors.toList());
    }

    public List<Long> bundleEventSpecialIds(Bundle bundle) {
        return bundle.getEventSpecials().stream()
          .map(EventSpecial::getEventSpecialId)
          .collect(Collectors.toList());
    }

    public List<Long> bundleEventTrackIds(Bundle bundle) {
        return bundle.getEventTracks().stream()
          .map(EventTrack::getEventTrackId)
          .collect(Collectors.toList());
    }


    public Set<EventSpecial> findSpecialsByBundle(Bundle bundle) {
        return bundle.getEventSpecials();
    }

}
