package ch.redanz.redanzCore.model.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Slf4j
@Table(name="bundle")
public class Bundle implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="bundle_id")
  private Long bundleId;

  private String name;
  private Integer price;
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
    Integer price,
    String description,
    Integer capacity
  ) {
    this.name = name;
    this.price = price;
    this.description = description;
    this.capacity = capacity;
  }

  public Long getBundleId() {
    return bundleId;
  }

  public void setBundleId(Long bundleId) {
    this.bundleId = bundleId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public List<BundleTrack> getBundleTracks() {
    return bundleTracks;
  }

  public void setTrackBundles(List<BundleTrack> bundleTracks) {
    this.bundleTracks = bundleTracks;
  }
}

