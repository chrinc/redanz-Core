package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "base_par")
public class BasePar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "base_par_id")
  private Long baseParId;
//
//  @Column(name = "base_par_key")
//  private String baseParKey;

  @Column(name = "val")
  private String val;

  @Column(name = "name")
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  public BasePar(String val, String name, Event event) {
    this.val = val;
    this.name = name;
    this.event = event;
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("val", val);
      }
    };
  }

}
