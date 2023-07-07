package ch.redanz.redanzCore.model.profile.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "country")
public class Country implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "sort_name")
  private String sortName;

  @Column(name = "name")
  private String name;

  @Column(name = "out_text_key")
  private String outTextKey;

  public Country(String sortName, String name, String outTextKey) {
    this.sortName = sortName;
    this.name = name;
    this.outTextKey = outTextKey;
  }
}


