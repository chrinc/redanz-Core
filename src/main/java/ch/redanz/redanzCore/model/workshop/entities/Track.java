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
@Getter
@Setter
@Slf4j
@Table(name = "track")
public class Track implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "track_id")
  private Long trackId;
  private String name;
  private String description;
  private Integer capacity;

  @Column(name = "partner_required")
  private Boolean partnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
  private List<TrackDanceRole> trackDanceRoles = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
  private List<TrackDiscount> trackDiscounts = new ArrayList<>();

  @Column(name = "sold_out")
  private boolean soldOut;

  public Track() {
  }

  public Track(
    String name,
    String description,
    Integer capacity,
    Boolean partnerRequired,
    DanceLevel danceLevel
  ) {
    this.name = name;
    this.description = description;
    this.capacity = capacity;
    this.partnerRequired = partnerRequired;
    this.requiredDanceLevel = danceLevel;
  }
}

