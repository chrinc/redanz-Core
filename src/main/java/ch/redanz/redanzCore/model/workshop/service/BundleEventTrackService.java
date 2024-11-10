package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.*;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class BundleEventTrackService {
  private final BundleEventTrackRepo bundleEventTrackRepo;
  private final EventTrackRepo eventTrackRepo;
  private final OutTextService outTextService;
  private final EventDanceRoleRepo eventDanceRoleRepo;
  private final BundleEventTrackDanceRoleRepo bundleEventTrackDanceRoleRepo;

  public void save(BundleEventTrack bundleEventTrack) {
    bundleEventTrackRepo.save(bundleEventTrack);
  }

  public void save(BundleEventTrackDanceRole bundleEventTrackDanceRole) {
    bundleEventTrackDanceRoleRepo.save(bundleEventTrackDanceRole);
  }

  public List<BundleEventTrack> findAllByEventAndBundle (Event event, Bundle bundle) {
    return bundleEventTrackRepo.findAllByBundleAndEventTrackEvent(bundle, event);
  }

  public BundleEventTrack findByEventBundleAndTrack(Event event, Bundle bundle, Track track) {
    return bundleEventTrackRepo.findByEventTrackEventAndBundleAndEventTrackTrack(event, bundle, track);
  }


  public BundleEventTrack findByBundleAndEventTrack(Bundle bundle, EventTrack eventTrack) {
    return bundleEventTrackRepo.findByBundleAndEventTrack(bundle, eventTrack);
  }

  public Field getField(String key) {
    Field field;
    try {
      field = BundleEventTrack.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateBundleEventTrack(JsonObject request, Bundle bundle) throws IOException, TemplateException {
    BundleEventTrack bundleEventTrack;

    Long bundleEventTrackId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    if (bundleEventTrackId == null || bundleEventTrackId == 0) {
      bundleEventTrack = new BundleEventTrack();
      bundleEventTrack.setBundle(bundle);
    } else {
      bundleEventTrack = bundleEventTrackRepo.findByBundleEventTrackId(bundleEventTrackId);
    }

    Set<EventDanceRole> newEventDanceRoles = new HashSet<>();

    BundleEventTrack.schema().forEach(
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
                  field.set(bundleEventTrack, outTextKey);
                }
              }
              break;
            case "text":
              field = getField(key);

              field.set(bundleEventTrack, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(bundleEventTrack, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(bundleEventTrack, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(bundleEventTrack, request.get(key).isJsonNull() ? null :
                request.get(key).isJsonObject() ? request.get(key).getAsJsonObject().get("hex").getAsString()
                  : request.get(key).getAsString())
              ;
              break;

            case "bool":
              field = getField(key);
              field.set(bundleEventTrack, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "listText":
              // tracks
              field = getField(key);
              switch (key) {
                case "eventTrack":
                  String id = request.get(key).getAsString();
                  field.set(bundleEventTrack, eventTrackRepo.findByEventTrackId(
                    request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString())
                  ));
                  break;
              }
              break;
            case "multiselectText":
              if (request.get(key) != null && request.get(key).isJsonArray()) {
                switch(key) {
                  case "bundleEventTrackDanceRoles":
                    request.get(key).getAsJsonArray().forEach(item -> {
                      newEventDanceRoles.add(eventDanceRoleRepo.findByEventDanceRoleId(item.getAsLong()));
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
    save(bundleEventTrack);

    Set<BundleEventTrackDanceRole> newBundleEventTrackDanceRoles = new HashSet<>();
    newEventDanceRoles.forEach(eventDanceRole -> {
      newBundleEventTrackDanceRoles.add(
        new BundleEventTrackDanceRole(
          bundleEventTrack
          , eventDanceRole
        )
      );
    });
    bundleEventTrackDanceRoleRepo.deleteAllByBundleEventTrack(bundleEventTrack);
    bundleEventTrack.setBundleEventTrackDanceRoles(newBundleEventTrackDanceRoles);
    save(bundleEventTrack);
  }

  public List<Long> bundleEventTrackEventDanceRoleIds(BundleEventTrack bundleEventTrack) {
    List<Long> bundleEventTrackDanceRoleIds = new ArrayList<>();
    bundleEventTrack.getBundleEventTrackDanceRoles().forEach(
      bundleEventTrackDanceRole -> {
        bundleEventTrackDanceRoleIds.add(bundleEventTrackDanceRole.getEventDanceRole().getEventDanceRoleId());
      }
    );
    return bundleEventTrackDanceRoleIds;
  }

  public void delete(BundleEventTrack bundleEventTrack) {
    bundleEventTrackRepo.delete(bundleEventTrack);
  }

  public void deleteBundleEventTrack(JsonObject request) {
//    log.info("delete");
    log.info(request.toString());
    delete(bundleEventTrackRepo.findByBundleEventTrackId(request.get("id").getAsLong()));
//    event.setScholarship(false);
//    eventRepo.save(event);
  }

  public void deleteAllByEventTrack(EventTrack eventTrack) {
    bundleEventTrackRepo.findAllByEventTrack(eventTrack).forEach(bundleEventTrack -> {
      bundleEventTrack.getBundle().getBundleEventTracks().remove(bundleEventTrack);
      delete(bundleEventTrack);
    });
  }
}
