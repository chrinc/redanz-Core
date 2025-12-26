package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventTrackRepo;
//import ch.redanz.redanzCore.model.workshop.repository.TrackDanceRoleRepo;
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
  private final DiscountService discountService;
  private final OutTextService outTextService;
  private final EventTrackRepo eventTrackRepo;
  private final DanceRoleService danceRoleService;
//  private final TrackDanceRoleRepo trackDanceRoleRepo;

  public void save(Track track) {
    trackRepo.save(track);
  }
//  public void save(TrackDanceRole trackDanceRole) {
//    trackDanceRoleRepo.save(trackDanceRole);
//  }

  public List<Track> getAll(){
    return trackRepo.findAll();
  }

  public boolean bundleHasTrack(Bundle bundle) {
    return !bundle.getBundleEventTracks().isEmpty();
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

  public void updateTrack(JsonObject request, Event event) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Track track;
    List<EventDiscount> newEventDiscounts = new ArrayList<>();
//    Set<TrackDanceRole> newTrackDanceRoles = new HashSet<>();
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

                if (outTextKey != null) {
                  field = getField(key);
                  field.set(track, outTextKey);
                }
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

              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(track, request.get(key).isJsonNull() ? null : localDate);
              break;


            case "bool":
              field = getField(key);
              field.set(track, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "multiselect":
            case "multiselectText":
//              log.info(eventPartKey);

              if (request.get(key) != null && request.get(key).isJsonArray()) {
                switch (key) {
                  case "discounts":
                    request.get(key).getAsJsonArray().forEach(item -> {
                      newEventDiscounts.add(discountService.findByEventDiscountId(item.getAsLong()));
                    });
                    break;
//                  case "danceRoles":
//                    request.get(eventPartKey).getAsJsonArray().forEach(item -> {
//                      newTrackDanceRoles.add(trackDanceRoleRepo.findByTrackDanceRoleId(item.getAsLong()));
//                    });
//                    break;

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
    track.setEventDiscounts(newEventDiscounts);
//    track.setTrackDanceRoles(newTrackDanceRoles);

    save(track);
//    newDanceRoles.forEach(
//      danceRole -> {
//    });

    if (trackIsNew) {
      eventTrackRepo.save(
        new EventTrack(
          track, event
        )
      );
    };
  }


//  public Field getTrackDanceRoleField(String key) {
//    Field field;
//    try {
//      field = TrackDanceRole.class.getDeclaredField(key);
//    } catch (NoSuchFieldException e) {
//      throw new RuntimeException(e);
//    }
//    field.setAccessible(true);
//    return field;
//  }
//
//  public void updateTrackDanceRole(JsonObject request) throws IOException, TemplateException {
//    Long trackDanceRoleId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
//    TrackDanceRole trackDanceRole;
//    if (trackDanceRoleId == null || trackDanceRoleId == 0) {
//      trackDanceRole = new TrackDanceRole();
//      trackDanceRole.setTrack(findByTrackId(request.get("trackId").isJsonNull() ? null : request.get("trackId").getAsLong()));
//    } else {
//      trackDanceRole = trackDanceRoleRepo.findByTrackDanceRoleId(trackDanceRoleId);
//    }
//
//    TrackDanceRole.schema().forEach(
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
//
//                if (outTextKey != null) {
//                  field = getTrackDanceRoleField(key);
//                  field.set(trackDanceRole, outTextKey);
//                }
//              }
//              break;
//            case "text":
//              field = getTrackDanceRoleField(key);
//              field.set(trackDanceRole, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
//              break;
//
//            case "number":
//              field = getTrackDanceRoleField(key);
//              field.set(trackDanceRole, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
//              break;
//
//            case "double":
//              field = getTrackDanceRoleField(key);
//              field.set(trackDanceRole, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
//              break;
//
//
//            case "color":
//              field = getTrackDanceRoleField(key);
//              field.set(trackDanceRole, request.get(key).isJsonNull() ? null :
//                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
//                  request.get(key).getAsJsonObject().get("hex").getAsString());
//              break;
//
//            case "bool":
//              field = getTrackDanceRoleField(key);
//              field.set(trackDanceRole, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
//              break;
//
//            case "listText":
//              field = getTrackDanceRoleField(key);
//              switch (key) {
//                case "danceRole":
//                  field.set(trackDanceRole, danceRoleService.findByDanceRoleId(
//                    request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString())
//                  ));
//                  break;
//              }
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
//    save(trackDanceRole);
//  }
//
//
  public void delete(Track track) {
//    track.getTrackDanceRoles().forEach(trackDanceRole -> delete(trackDanceRole));
    track.setEventDiscounts(new ArrayList<>());
    trackRepo.delete(track);
  }
//
//  public void delete(TrackDanceRole trackDanceRole) {
//    trackDanceRole.setDanceRole(null);
//    trackDanceRoleRepo.delete(trackDanceRole);
//  }


  public List<Long> trackEventDiscountIdList(Track track){
    List<Long> trackEventDiscountIdList = new ArrayList<>();
    track.getEventDiscounts().forEach(eventDiscount -> {
      trackEventDiscountIdList.add(eventDiscount.getEventDiscountId());
    });

    return trackEventDiscountIdList;
  }

//  public List<Long> trackDanceRoleIdList(Track track){
//    return track.getTrackDanceRoles().stream()
//      .map(TrackDanceRole::getTrackDanceRoleId)
//      .collect(Collectors.toList());
//  }


  public List<Map<String, String>> getTrackSchema(Event event){
    List<Map<String, String>> trackSchema = Track.schema();
    trackSchema.forEach(item -> {
      if (item.get("key").equals("discounts")) {
        item.put("list", discountService.getDiscountsMap(event).toString());
      }
      if (item.get("key").equals("danceRoles")) {
        item.put("list", danceRoleService.getDanceRolesMap().toString());
      }
    });
    return trackSchema;
  }
//  public List<Map<String, String>> getTrackDanceRoleSchema(Event event){
//    List<Map<String, String>> trackDanceRoleSchema = TrackDanceRole.schema();
//    trackDanceRoleSchema.forEach(item -> {
//      switch (item.get("key")) {
//        case "danceRole":
//          item.put("list", getEventDanceRolesMap(event).toString());
//          break;
//      }
//    });
//    return trackDanceRoleSchema;
//  }

  public List<Map<String, String>> getEventDanceRolesMap(Event event) {
    List<Map<String, String>> eventDanceRoles = new ArrayList<>();
    event.getEventDanceRoles().forEach(eventDanceRole -> {
      eventDanceRoles.add(eventDanceRole.getDanceRole().dataMap());
    });
    return eventDanceRoles;
  }

//  public List<Map<String, String>> getTrackDanceRoleData(Track track) {
//    List<Map<String, String>> trackDanceRolesData = new ArrayList<>();
//    track.getTrackDanceRoles().forEach(
//      trackDanceRole -> {
//        Map<String, String> trackDanceRoleData = trackDanceRole.dataMap();
////        trackDanceRoleData.put("danceRole", Long.toString(trackDanceRole.getTrackDanceRoleId()));
////        trackDanceRoleData.put("danceRoles", trackEventDiscountIdList(eventTrack.getTrack()).toString());
//        trackDanceRolesData.add(trackDanceRoleData);
//      });
//    return trackDanceRolesData;
//  }

  public List<Map<String, String>> getTracksData(Event event) {
    List<Map<String, String>> tracksData = new ArrayList<>();
    event.getEventTracks().forEach(
      eventTrack -> {
        Map<String, String> trackData = eventTrack.getTrack().dataMap();
        trackData.put("eventId", Long.toString(event.getEventId()));
        trackData.put("discounts", trackEventDiscountIdList(eventTrack.getTrack()).toString());
//        trackData.put("capacity", bundleEventTrackCapacity());
//        trackData.put("danceRoles", trackDanceRoleIdList(eventTrack.getTrack()).toString());
        tracksData.add(trackData);
      });
    return tracksData;
  }

    public List<Map<String, String>> getEventTracksData(Event event) {
    List<Map<String, String>> eventTracksData = new ArrayList<>();
    event.getEventTracks().forEach(
      eventTrack -> {
        Map<String, String> eventTrackData = eventTrack.dataMap();
        eventTrackData.put("eventId", Long.toString(event.getEventId()));
        eventTrackData.put("name",  eventTrack.getTrack().getName());
        eventTrackData.put("trackId",  eventTrack.getTrack().getTrackId().toString());
//        eventTrackData.put("track", eventTrack.getTrack().simpleDataMap().toString());
//        eventTrackData.put("bundleId", trackEventDiscountIdList(eventTrack.getTrack()).toString());
//        trackData.put("capacity", bundleEventTrackCapacity());
//        trackData.put("danceRoles", trackDanceRoleIdList(eventTrack.getTrack()).toString());
        eventTracksData.add(eventTrackData);
      });
    return eventTracksData;
  }

  public EventTrack clone(Event newEvent, Track baseTrack) {
    Track newTrack = new Track(
      baseTrack.getName(),
      baseTrack.getDescription(),
//      baseTrack.getCapacity(),
      baseTrack.getPartnerRequired(),
      baseTrack.getOwnPartnerRequired(),
      baseTrack.getRequiredDanceLevel()
    );

    // clone EventDiscounts
    List<EventDiscount>  newEventDiscounts = new ArrayList<>(newEvent.getEventDiscounts());
    Set<Discount> oldDiscounts = baseTrack.getEventDiscounts().stream()
      .map(EventDiscount::getDiscount)
      .collect(Collectors.toSet());

    newEventDiscounts.stream()
      .filter(eventDiscount -> oldDiscounts.contains(eventDiscount.getDiscount()))
      .collect(Collectors.toList());
    newTrack.setEventDiscounts(newEventDiscounts);

//    // clone Dance Roles
//    Set<TrackDanceRole>  newTrackDanceRoles = new HashSet<>(baseTrack.getTrackDanceRoles());
//    Set<DanceRole> oldDanceRoles = baseTrack.getTrackDanceRoles().stream()
//      .map(TrackDanceRole::getDanceRole)
//      .collect(Collectors.toSet());
//    newTrackDanceRoles.stream().filter(trackDanceRole -> oldDanceRoles.contains(trackDanceRole.getDanceRole()))
//        .collect(Collectors.toSet());
//    newTrack.setTrackDanceRoles(newTrackDanceRoles);

    // clone EventTracks
    EventTrack newEventTrack = new EventTrack(newTrack, newEvent);
    newEvent.getEventTracks().add(newEventTrack);
    save(newTrack);
    eventTrackRepo.save(newEventTrack);
    return newEventTrack;
  }

//  public void deleteTrackDanceRole(JsonObject request) {
//    Long trackDanceRole = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
//    delete(trackDanceRoleRepo.findByTrackDanceRoleId(trackDanceRole));
//  }


}
