package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "private_class")
public class PrivateClass implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "private_class_id")
  private Long privateClassId;
  private String name;
  private String description;
  private Integer capacity;
  private double price;

  @Column(name = "partner_required")
  private Boolean partnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  @Column(name = "sold_out")
  private Boolean soldOut;

  public PrivateClass() {
  }

  public PrivateClass(
    String name,
    String description,
    Integer capacity,
    double price,
    Boolean partnerRequired,
    DanceLevel danceLevel
  ) {
    this.name = name;
    this.description = description;
    this.capacity = capacity;
    this.price = price;
    this.partnerRequired = partnerRequired;
    this.requiredDanceLevel = danceLevel;
  }
}
