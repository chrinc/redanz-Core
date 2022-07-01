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
@Table(name = "track_discount")
public class TrackDiscount implements Serializable {

  @EmbeddedId
  private final TrackDiscountId trackDiscountId = new TrackDiscountId();

  @ManyToOne
  @MapsId("discountId")
  @JoinColumn(name = "discount_id")
  private Discount discount;

  @ManyToOne
  @JsonIgnore
  @MapsId("trackId")
  @JoinColumn(name = "track_id")
  private Track track;

  public TrackDiscount() {
  }

  public TrackDiscount(Discount discount, Track track) {
    this.discount = discount;
    this.track = track;
  }
}
