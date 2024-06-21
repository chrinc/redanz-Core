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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "food_id")
  private Food food;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "slot_id")
  private Slot slot;

  @Column(name = "price")
  private double price;

  @Column(name = "seq_nr")
  private Integer seqNr;

  public EventFoodSlot() {
  }
  public EventFoodSlot(Food food, Slot slot, Event event, double price, Integer seqNr) {
    this.food = food;
    this.slot = slot;
    this.event = event;
    this.price = price;
    this.seqNr = seqNr;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "food");                 put("type", "list");    put("required", "true");   put("label", "Food");    put("list", null);}});
        add(new HashMap<>() {{put("key", "slot");                 put("type", "list");    put("required", "true");   put("label", "Slot");    put("list", null);}});
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
        put("foodSlot", null);
        put("price", String.valueOf(price));
        put("seqNr", String.valueOf(seqNr));
      }
    };
  }
}

