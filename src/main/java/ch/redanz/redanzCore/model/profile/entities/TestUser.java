package ch.redanz.redanzCore.model.profile.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "test_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestUser {
  @Id
  private Long id;

  private String username;
}
