package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.EventPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventPartRepo extends JpaRepository<EventPart, Long> {
  EventPart findByName(String name);
  EventPart findByEventPartKey(String eventPartKey);
}
