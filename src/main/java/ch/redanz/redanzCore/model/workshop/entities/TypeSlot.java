package ch.redanz.redanzCore.model.workshop.entities;


import ch.redanz.redanzCore.model.registration.entities.Registration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "type_slot")
public class TypeSlot {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "type_slot_id")
  private Long typeSlotId;

  @JsonIgnore
  private String type;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "slot_id")
  private Slot slot;

  @Column(name = "object_type_id")
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



  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "food");                 put("type", "listInfo");  put("required", "true"); put("label", "Food"); put("list", null); put("infoKey", "price");}});
        add(new HashMap<>() {{put("key", "slot");                 put("type", "list");    put("required", "true");   put("label", "Slot"); put("list", null);}});
        add(new HashMap<>() {{put("key", "seqNr");                put("type", "double");  put("required", "true");   put("label", "Sequence Number");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(typeSlotId));
        put("food", null);
        put("slot", null);
        put("seqNr", null);
      }
    };
  }
}
