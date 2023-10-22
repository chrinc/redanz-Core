package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @ManyToMany
  @JoinTable(
    name = "bundle_special",
    joinColumns = @JoinColumn(name = "bundle_id"),
    inverseJoinColumns = @JoinColumn(name = "special_id"))
  private List<Special> specials;


  @ManyToMany(cascade=CascadeType.ALL)
  @JoinTable(
    name="bundle_party_slots",
    joinColumns = @JoinColumn(name="bundle_id"),
    inverseJoinColumns = @JoinColumn(name="slot_id")
  )
  private List<Slot> partySlots;

  private String name;

  @Column(name = "internal_id")
  private String internalId;

  private double price;
  private String description;
  private Integer capacity;
  private Boolean simpleTicket;

  @Column(name = "sold_out")
  private boolean soldOut;

  private String color;

  @Column(name = "seq_nr")
  private Integer seqNr;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "bundle")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<BundleTrack> bundleTracks = new ArrayList<>();

  public Bundle() {
  }

  public Bundle(
    String name,
    double price,
    String description,
    Integer capacity,
    Boolean simpleTicket,
    String internalId,
    Integer seqNr,
    List<Slot> partySlots,
    String color
  ) {
    this.name = name;
    this.price = price;
    this.description = description;
    this.capacity = capacity;
    this.simpleTicket = simpleTicket;
    this.internalId = internalId;
    this.seqNr = seqNr;
    this.partySlots = partySlots;
    this.color = color;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{
          put("key", "eventId");
          put("type", "id");
          put("label", "EventId");
        }});
        add(new HashMap<>() {{
          put("key", "bundleId");
          put("type", "id");
          put("label", "BundleId");
        }});
        add(new HashMap<>() {{
          put("key", "name");
          put("type", "text");
          put("label", "Name");
        }});
        add(new HashMap<>() {{
          put("key", "price");
          put("type", "number");
          put("label", "Price");
        }});
        add(new HashMap<>() {{
          put("key", "capacity");
          put("type", "number");
          put("label", "Capacity");
        }});
        add(new HashMap<>() {{
          put("key", "simpleTicket");
          put("type", "boolean");
          put("label", "simpleTicket");
        }});
        add(new HashMap<>() {{
          put("key", "description");
          put("type", "text");
          put("label", "Description");
        }});
        add(new HashMap<>() {{
          put("key", "isEdit");
          put("type", "isEdit");
          put("label", "");
        }});
        add(new HashMap<>() {{
          put("key", "internalId");
          put("type", "internalId");
          put("label", "Internal ID");
        }});

        add(new HashMap<>() {{
          put("key", "color");
          put("type", "color");
          put("label", "Wrist Band Color");
        }});
        add(new HashMap<>() {{
          put("key", "seqNr");
          put("type", "seqNr");
          put("label", "Sequence Number");
        }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("bundleId", Long.toString(bundleId));
        put("name", name);
        put("price", String.valueOf(price));
        put("capacity", String.valueOf(capacity));
        put("description", description);
        put("simpleTicket", simpleTicket.toString());
        put("seqNr", String.valueOf(seqNr));
      }
    };
  }
}
