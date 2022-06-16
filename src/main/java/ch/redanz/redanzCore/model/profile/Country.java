package ch.redanz.redanzCore.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="country")
public class Country implements Serializable {

  @Id
  private Long id;

  @Column(name = "sort_name")
  private String sortName;

  @Column(name = "name_ge")
  private String nameGe;

  @Column(name = "name_en")
  private String nameEn;

  @OneToMany
  @JoinColumn(name = "person_id")
  @JsonIgnore
  private List<Person> person;
}


