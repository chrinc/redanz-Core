package ch.redanz.redanzCore.model.registration.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

  @Column(name = "base_par_key")
  private String baseParKey;

  @Column(name = "bool_val")
  private Boolean boolValue;

  @Column(name = "string_val")
  private String stringValue;

  public BasePar(String baseParKey, Boolean boolValue, String stringValue) {
    this.baseParKey = baseParKey;
    this.boolValue = boolValue;
    this.stringValue = stringValue;
  }

}
