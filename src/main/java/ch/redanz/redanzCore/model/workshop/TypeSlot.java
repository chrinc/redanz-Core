package ch.redanz.redanzCore.model.workshop;


import ch.redanz.redanzCore.model.profile.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="type_slot")
public class TypeSlot {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "volunteer_slot_id")
  private Long typeSlotId;

  @JsonIgnore
  private String type;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="slot_id")
  private Slot slot;

  @Column(name="object_type_id")
  private Long typeObjectId;

  public TypeSlot(String type, Slot slot) {
    this.type = type;
    this.slot = slot;
  }

  public TypeSlot(String type, Slot slot, Long typeObjectId) {
    this.type = type;
    this.slot = slot;
    this.typeObjectId = typeObjectId;
  }
}