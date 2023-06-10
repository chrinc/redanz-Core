package ch.redanz.redanzCore.model.workshop.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_discount")
public class EventDiscount implements Serializable {

  @EmbeddedId
  private final EventDiscountId eventDiscountId = new EventDiscountId();

  @ManyToOne
  @MapsId("discountId")
  @JoinColumn(name = "discount_id")
  private Discount discount;

  @ManyToOne()
  @JsonIgnore
  @MapsId("eventId")
  @JoinColumn(name = "event_id")
  private Event event;

  public EventDiscount() {
  }
  public EventDiscount(Discount discount, Event event) {
    this.discount = discount;
    this.event = event;
  }
}

