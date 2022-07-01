package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.TrackDiscount;
import ch.redanz.redanzCore.model.workshop.entities.TrackDiscountId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackDiscountRepo extends JpaRepository<TrackDiscount, TrackDiscountId> {
}
