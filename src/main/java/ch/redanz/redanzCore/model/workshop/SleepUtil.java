package ch.redanz.redanzCore.model.workshop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Table(name="sleep_util")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SleepUtil implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="sleep_util_id")
  private Long sleepUtilId;

  private String name;

  public SleepUtil(String name) {
    this.name = name;
  }
}
