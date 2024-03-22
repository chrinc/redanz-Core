package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.entities.TrackDiscount;
import ch.redanz.redanzCore.model.workshop.entities.TrackDiscountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackDiscountRepo extends JpaRepository<TrackDiscount, TrackDiscountId> {
  boolean existsByTrackAndDiscount(Track track, Discount discount);
  List<TrackDiscount> findAllByTrack(Track track);
  TrackDiscount findByDiscountAndTrack(Discount discount, Track track);

  @Override
  @Modifying
  @Query("delete from TrackDiscount t where t.trackDiscountId = ?1")
  void deleteById(TrackDiscountId trackDiscountId);
}
