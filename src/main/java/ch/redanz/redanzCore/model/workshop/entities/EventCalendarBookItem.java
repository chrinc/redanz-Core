package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.config.BookItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_calendar_book_item")
public class EventCalendarBookItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_calendar_id")
  @JsonIgnore
  private EventCalendar eventCalendar;

  @Column(name = "book_item_id")
  private Long bookItemId;

  @Enumerated(EnumType.STRING)
  @Column(name = "book_item_type")
  private BookItemType bookItemType;

  public EventCalendarBookItem() {}
  public EventCalendarBookItem(EventCalendar eventCalendar, Long bookItemId, BookItemType bookItemType) {
    this.eventCalendar = eventCalendar;
    this.bookItemId = bookItemId;
    this.bookItemType = bookItemType;
  }
}
