package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Slf4j
@Getter
@Setter
@Table(name = "bundle")
public class Bundle implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "bundle_id")
  private Long bundleId;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "bundle_event_special",
    joinColumns = @JoinColumn(name = "bundle_id"),
    inverseJoinColumns = @JoinColumn(name = "event_special_id"))
  private Set<EventSpecial> eventSpecials;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "bundle_event_track",
    joinColumns = @JoinColumn(name = "bundle_id"),
    inverseJoinColumns = @JoinColumn(name = "event_track_id"))
  private Set<EventTrack> eventTracks;


  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name="bundle_party_slots",
    joinColumns = @JoinColumn(name="bundle_id"),
    inverseJoinColumns = @JoinColumn(name="slot_id")
  )
  private Set<Slot> partySlots;

  private String name;

  private double price;
  private String description;
  private Integer capacity;
  private Boolean simpleTicket;

  @Column(name = "sold_out")
  private boolean soldOut;

  private String color;

  @Column(name = "seq_nr")
  private Integer seqNr;

  @Column(name = "active")
  private Boolean active;

  public Bundle() {
  }

  public Bundle(
    String name,
    double price,
    String description,
    Integer capacity,
    Boolean simpleTicket,
    Integer seqNr,
    Set<Slot> partySlots,
    String color,
    Boolean active
  ) {
    this.name = name;
    this.price = price;
    this.description = description;
    this.capacity = capacity;
    this.simpleTicket = simpleTicket;
    this.seqNr = seqNr;
    this.partySlots = partySlots;
    this.color = color;
    this.active = active;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "eventId");       put("type", "id");                                        put("label", "EventId");          }});
        add(new HashMap<>() {{put("key", "id");            put("type", "id");                                        put("label", "Bundle Id");        }});
        add(new HashMap<>() {{put("key", "name");          put("type", "text");            put("required", "true");  put("label", "Name");             }});
        add(new HashMap<>() {{put("key", "price");         put("type", "number");          put("required", "true");  put("label", "Price");            }});
        add(new HashMap<>() {{put("key", "capacity");      put("type", "number");          put("required", "true");  put("label", "Capacity");         }});
        add(new HashMap<>() {{put("key", "description");   put("type", "label");           put("required", "true");  put("label", "Description");      }});
        add(new HashMap<>() {{put("key", "simpleTicket");  put("type", "bool");                                      put("labelTrue", "Simple Ticket");        put("labelFalse", "Regular Bundle"); }});
        add(new HashMap<>() {{put("key", "active");        put("type", "bool");                                      put("labelTrue", "Active");               put("labelFalse", "Inactive"); }});
        add(new HashMap<>() {{put("key", "color");         put("type", "color");           put("required", "false"); put("label", "Wrist Band Color"); }});
        add(new HashMap<>() {{put("key", "bundleSpecial"); put("type", "multiselectInfo");                           put("label", "Specials");                 put("infoKey", "price");}});
        add(new HashMap<>() {{put("key", "track");         put("type", "multiselectText"); put("required", "false"); put("label", "Track");}});
        add(new HashMap<>() {{put("key", "partySlots");    put("type", "multiselect");     put("required", "false"); put("label", "Party Slots");              put("list", null);}});
        add(new HashMap<>() {{put("key", "seqNr");         put("type", "number");          put("required", "true");  put("label", "Sequence Number");  }});
        add(new HashMap<>() {{put("key", "eventPartInfo"); put("type", "partInfo");        put("eventPartKey", "bundle");                          put("label", OutTextConfig.LABEL_BUNDLE_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "clone");         put("type", "action");          put("show", "true");}});
        add(new HashMap<>() {{put("key", "plural");        put("type", "title");           put("label", OutTextConfig.LABEL_BUNDLES_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");      put("type", "title");           put("label", OutTextConfig.LABEL_BUNDLE_EN.getOutTextKey()); }});
     }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", Long.toString(bundleId));
        put("name", name);
        put("price", String.valueOf(price));
        put("capacity", String.valueOf(capacity));
        put("description", description);
        put("simpleTicket", simpleTicket.toString());
        put("color", color);
        put("seqNr", String.valueOf(seqNr));
        put("partySlots", null);
        put("active", String.valueOf(active));
      }
    };
  }
}
