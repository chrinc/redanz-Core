package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.repository.GuestRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@AllArgsConstructor
@Slf4j
public class GuestService {
  private final GuestRepo guestRepo;
  private final SlotService slotService;


  public List<Guest> findAllByEvent(Event event) {
    return guestRepo.findAllByEventAndActive(event, true);

  }

  public List<Guest> findAllByEventAndSlot(Event event, Slot slot) {
    return guestRepo.findAllByEventAndActiveAndSlotsContains(event, true, slot);
  }


  public void save(Guest guest) {
    guestRepo.save(guest);
  }
  @Transactional
  public void removeGuest(JsonObject guest, Event event) throws IOException, TemplateException {
    log.info("inc@start remove");
    if (
      guest.getAsJsonObject().get("guestId") != null
        && !guest.getAsJsonObject().get("guestId").isJsonNull()
    ) {

      // remove
      Guest myExistingGuest = guestRepo.findById(guest.getAsJsonObject().get("guestId").getAsLong()).get();
      myExistingGuest.setActive(false);
      save(myExistingGuest);
    }
  }


  @Transactional
  public void updateGuestRequest(JsonObject guest, Event event) throws IOException, TemplateException {
      log.info("inc@start update");
      AtomicBoolean doUpdate = new AtomicBoolean(false);

      JsonObject myGuest = guest.getAsJsonObject();
      String name = myGuest.get("name").isJsonNull() ? "" : myGuest.get("name").getAsString();
      String description = myGuest.get("description").isJsonNull() ? "" : myGuest.get("description").getAsString();
      List<Slot> newSlots = new ArrayList<>();
      JsonArray mySlots = myGuest.get("slots").isJsonNull() ? null : myGuest.get("slots").getAsJsonArray();
      if (mySlots != null) {
        mySlots.forEach(mySlot -> {
          newSlots.add(slotService.findBySlotId(mySlot.getAsLong()));
        });
      }
      if (
          guest.getAsJsonObject().get("guestId") != null
       && !guest.getAsJsonObject().get("guestId").isJsonNull()
      ) {

        // update
        Guest myExistingGuest = guestRepo.findById(guest.getAsJsonObject().get("guestId").getAsLong()).get();
        if (
          !myExistingGuest.getName().equals(name)
        ) {
          myExistingGuest.setName(name);
          doUpdate.set(true);
        }

        if (
          !myExistingGuest.getDescription().equals(description)
        ) {
          myExistingGuest.setDescription(description);
          doUpdate.set(true);
        }
        List<Slot> updateSlots = new ArrayList<>();
        newSlots.forEach(newSlot -> {
          if (!myExistingGuest.getSlots().contains(newSlot)) {
            updateSlots.add(newSlot);
            doUpdate.set(true);
          }
        });

        myExistingGuest.getSlots().forEach(existingSlot -> {
          if (!newSlots.contains(existingSlot)) {
            doUpdate.set(true);
          }
        });
        if (doUpdate.get()) {
          myExistingGuest.setSlots(newSlots);
          save(myExistingGuest);
        }


      } else {
        save(
          new Guest(
            name,
            description,
            event,
            newSlots,
            true
          )
        );
      }
  }
  @Transactional
  public void updateGuestListRequest(JsonArray request, Event event) throws IOException, TemplateException {

    request.forEach(guest -> {
      AtomicBoolean doUpdate = new AtomicBoolean(false);

      try {
        updateGuestRequest(guest.getAsJsonObject(), event);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (TemplateException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public List<Map<String, String>> getSchema(){
    return Guest.schema();
  }

}
