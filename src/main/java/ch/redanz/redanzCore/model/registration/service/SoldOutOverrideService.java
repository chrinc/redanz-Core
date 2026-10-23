package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class SoldOutOverrideService {
  private final EventService eventService;
  private final RegistrationService registrationService;
  private final EventBundleRepo eventBundleRepo;
  private final BundleEventTrackRepo bundleEventTrackRepo;
  private final BundleEventTrackDanceRoleRepo bundleEventTrackDanceRoleRepo;
  private final EventSpecialRepo eventSpecialRepo;
  private final EventPrivateClassRepo eventPrivateClassRepo;

  public enum ItemType {
    EVENT,
    EVENT_BUNDLE,
    BUNDLE_EVENT_TRACK,
    EVENT_SPECIAL,
    EVENT_PRIVATE_CLASS
  }

  // override: null = computed, true = sold out, false = open
  public void setOverride(Event event, ItemType itemType, Long itemId, Boolean override) {
    switch (itemType) {
      case EVENT -> {
        checkEvent(event, event.getEventId().equals(itemId) ? event : null);
        event.setSoldOutOverride(override);
        eventService.save(event);
      }
      case EVENT_BUNDLE -> {
        EventBundle eventBundle = eventBundleRepo.findById(itemId).orElseThrow();
        checkEvent(event, eventBundle.getEvent());
        eventBundle.setSoldOutOverride(override);
        eventBundleRepo.save(eventBundle);
      }
      case BUNDLE_EVENT_TRACK -> {
        BundleEventTrack bundleEventTrack = bundleEventTrackRepo.findById(itemId).orElseThrow();
        checkEvent(event, bundleEventTrack.getEventTrack().getEvent());
        bundleEventTrack.setSoldOutOverride(override);
        bundleEventTrackRepo.save(bundleEventTrack);
      }
      case EVENT_SPECIAL -> {
        EventSpecial eventSpecial = eventSpecialRepo.findById(itemId).orElseThrow();
        checkEvent(event, eventSpecial.getEvent());
        eventSpecial.setSoldOutOverride(override);
        eventSpecialRepo.save(eventSpecial);
      }
      case EVENT_PRIVATE_CLASS -> {
        EventPrivateClass eventPrivateClass = eventPrivateClassRepo.findById(itemId).orElseThrow();
        checkEvent(event, eventPrivateClass.getEvent());
        eventPrivateClass.setSoldOutOverride(override);
        eventPrivateClassRepo.save(eventPrivateClass);
      }
    }

    // bundles and event are aggregated from the effective values of their children
    registrationService.updateSoldOut(event);
  }

  // mode: null = computed
  public void setDanceRoleMode(Event event, Long bundleEventTrackDanceRoleId, DanceRoleSoldOutStatus mode) {
    BundleEventTrackDanceRole danceRole = bundleEventTrackDanceRoleRepo.findById(bundleEventTrackDanceRoleId).orElseThrow();
    checkEvent(event, danceRole.getBundleEventTrack().getEventTrack().getEvent());
    danceRole.setSoldOutMode(mode);
    bundleEventTrackDanceRoleRepo.save(danceRole);
  }

  private void checkEvent(Event event, Event itemEvent) {
    if (itemEvent == null || !Objects.equals(event.getEventId(), itemEvent.getEventId())) {
      throw new ApiRequestException("Sold out item does not belong to event " + event.getEventId());
    }
  }
}
