package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
}

