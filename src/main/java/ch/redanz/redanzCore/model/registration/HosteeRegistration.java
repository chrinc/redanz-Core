package ch.redanz.redanzCore.model.registration;

import ch.redanz.redanzCore.model.workshop.SleepUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="hostee_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HosteeRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "workflow_status_id")
  private Long hostRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="registration_id")
  @JsonIgnore
  private Registration registration;

  @Column(name="is_shared_rooms")
  private boolean isSharedRooms;

  @Column(name="name_room_mate")
  private String nameRoomMate;

  @Column(name="is_shared_bed")
  private boolean isSharedBed;
  @Column(name="comment")
  private String  comment;

  public HosteeRegistration(Registration registration, boolean isSharedRooms, String nameRoomMate, boolean isSharedBed, String comment) {
    this.registration = registration;
    this.isSharedRooms = isSharedRooms;
    this.nameRoomMate = nameRoomMate;
    this.isSharedBed = isSharedBed;
    this.comment = comment;
  }
}
