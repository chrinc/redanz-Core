package ch.redanz.redanzCore.model.workshop.entities;


import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_food_slot")
public class EventFoodSlot implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_food_slot_id")
  private Long eventFoodSlotId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  private String name;

  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_slot_id")
  private EventSlot eventSlot;

  @Column(name = "price")
  private double price;

  @Column(name = "seq_nr")
  private Integer seqNr;

  public EventFoodSlot() {
  }
  public EventFoodSlot(String name, String description, EventSlot eventSlot, Event event, double price, Integer seqNr) {
    this.name = name;
    this.description = description;
    this.eventSlot = eventSlot;
    this.event = event;
    this.price = price;
    this.seqNr = seqNr;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");   put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");   put("required", "true");   put("label", "Description");}});
        add(new HashMap<>() {{put("key", "eventSlot");            put("type", "list");    put("required", "true");   put("label", "Slot");    put("list", null);}});
        add(new HashMap<>() {{put("key", "price");                put("type", "double");  put("required", "true");   put("label", "Price");}});
        add(new HashMap<>() {{put("key", "seqNr");                put("type", "number");  put("required", "true");   put("label", "Sequence");}});
        add(new HashMap<>() {{put("key", "eventPartInfo"); put("type", "partInfo");        put("eventPartKey", "food"); put("label", OutTextConfig.LABEL_FOOD_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "plural");               put("type", "title");   put("label", OutTextConfig.LABEL_FOOD_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");             put("type", "title");   put("label", OutTextConfig.LABEL_FOOD_EN.getOutTextKey()); }});

      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventFoodSlotId));
        put("eventSlot", null);
        put("price", String.valueOf(price));
        put("seqNr", String.valueOf(seqNr));
      }
    };
  }
}

