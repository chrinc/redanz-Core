package ch.redanz.redanzCore.model.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.websocket.server.ServerEndpoint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Slf4j
@Getter
@Setter
@Table(name="bundle")
public class Bundle implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="bundle_id")
  private Long bundleId;

  private String name;
  private double price;
  private String description;

  private Integer capacity;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "bundle")
  @JsonIgnore
  private final List<EventBundle> eventBundles = new ArrayList<>();

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "bundle")
  private List<BundleTrack> bundleTracks = new ArrayList<>();

  public Bundle () {}

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
}

