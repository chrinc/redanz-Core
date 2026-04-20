package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.config.BookItemType;
import ch.redanz.redanzCore.model.workshop.config.SlotType;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_slot")
public class EventSlot implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_slot_id")
  private Long eventSlotId;

  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @Column(name = "seq_nr")
  private Integer seqNr;

  private String color;

  @Enumerated(EnumType.STRING)
  @Column(name = "slot_type")
  private SlotType slotType;

  @Column(name = "slot_from")
  private ZonedDateTime slotFrom;

  @Column(name = "slot_to")
  private ZonedDateTime slotTo;

  public EventSlot() {}

  public EventSlot(String name, Event event, Integer seqNr, String color, SlotType slotType, ZonedDateTime slotFrom, ZonedDateTime slotTo) {
    this.name = name;
    this.event = event;
    this.seqNr = seqNr;
    this.color = color;
    this.slotType = slotType;
    this.slotFrom = slotFrom;
    this.slotTo = slotTo;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap() {{
          put("key", "id");
          put("type", "id");
          put("label", "Id");
        }});
        add(new HashMap<>() {{
          put("key", "name");
          put("type", "label");
          put("required", "true");
          put("label", "Name");
        }});
        add(new HashMap<>() {{
          put("key", "color");
          put("type", "color");
          put("required", "false");
          put("label", "Color");
        }});
        add(new HashMap<>() {{put("key", "seqNr");    put("type", "number");    put("required", "true");   put("label", "Sequence Number");}});
        add(new HashMap<>() {{ put("key", "slotType");  put("type", "list");        put("required", "true");      put("label", "Slot Type");  put("list", null);}});
        add(new HashMap<>() {{put("key", "slotFrom");    put("type", "datetimeallday");    put("required", "false");   put("label", "Slot From");}});
        add(new HashMap<>() {{put("key", "slotTo");    put("type", "datetimeallday");    put("required", "false");   put("label", "Slot To");}});
        add(new HashMap<>() {{put("key", "plural");               put("type", "title");   put("label", OutTextConfig.LABEL_SLOTS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");             put("type", "title");   put("label", OutTextConfig.LABEL_SLOT_EN.getOutTextKey()); }});
      }
    };
  }


  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventSlotId));
        put("name", name);
        put("color", color);
        put("slotType", String.valueOf(slotType.getCode()));
        put("seqNr", String.valueOf(seqNr));
//        put("slotFrom", slotFrom != null ? slotFrom.toString() + "T00:00+0100" : null);
//        put("slotTo", slotTo != null ? slotTo.toString() + "T00:00+0100" : null);
      }
    };
  }
}
