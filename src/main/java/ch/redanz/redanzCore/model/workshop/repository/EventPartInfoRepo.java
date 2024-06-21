package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPartInfoRepo extends JpaRepository<EventPartInfo, Long> {
  EventPartInfo findByEventAndEventPart(Event event, EventPart eventPart);
  EventPartInfo findByEventPartInfoId(Long eventPartInfoId);
  List<EventPartInfo> findAllByEvent(Event event);
}
