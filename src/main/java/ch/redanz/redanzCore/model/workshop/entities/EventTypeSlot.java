package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_type_slot")
public class EventTypeSlot implements Serializable {

  @EmbeddedId
  private final EventTypeSlotId eventTypeSlotId = new EventTypeSlotId();

  @ManyToOne
  @MapsId("typeSlotId")
  @JoinColumn(name = "type_slot_id")
  private TypeSlot typeSlot;

  @ManyToOne()
  @JsonIgnore
  @MapsId("eventId")
  @JoinColumn(name = "event_id")
  private Event event;

  public EventTypeSlot() {
  }

  public EventTypeSlot(TypeSlot typeSlot, Event event) {
    this.typeSlot = typeSlot;
    this.event = event;
  }
}
