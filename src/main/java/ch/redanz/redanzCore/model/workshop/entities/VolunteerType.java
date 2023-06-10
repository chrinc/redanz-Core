package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "volunteer_type")
public class VolunteerType implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "volunteer_type_id")
  private Long volunteerTypeId;
  private String name;
  private String description;

  public VolunteerType() {
  }
  public VolunteerType(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
