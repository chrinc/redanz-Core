package ch.redanz.redanzCore.model.profile.entities;

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
@Table(name = "field_property")
public class FieldProperty {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "field_key")
  private String fieldKey;
  private String name;
  private boolean active;
  private boolean required;
  private String type;

  @Column(name = "listSource")
  private String listSource;

  public FieldProperty(
    String fieldKey,
    String name,
    boolean active,
    boolean required,
    String type,
    String listSource
  ) {
    this.fieldKey = fieldKey;
    this.name = name;
    this.active = active;
    this.required = required;
    this.type = type;
    this.listSource = listSource;
  }
}
