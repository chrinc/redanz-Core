package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.TrackDiscount;
import ch.redanz.redanzCore.model.workshop.TrackDiscountId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackDiscountRepo extends JpaRepository<TrackDiscount, TrackDiscountId> {
}
