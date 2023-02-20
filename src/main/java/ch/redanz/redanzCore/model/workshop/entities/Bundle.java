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

  private String name;
  private double price;
  private String description;
  private Integer capacity;

  @Column(name = "sold_out")
  private boolean soldOut;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "bundle")
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<BundleTrack> bundleTracks = new ArrayList<>();

  public Bundle() {
  }

  public Bundle(
    String name,
    double price,
    String description,
    Integer capacity
  ) {
    this.name = name;
    this.price = price;
    this.description = description;
    this.capacity = capacity;
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
          put("key", "description");
          put("type", "text");
          put("label", "Description");
        }});
        add(new HashMap<>() {{
          put("key", "isEdit");
          put("type", "isEdit");
          put("label", "");
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
      }
    };
  }
}
