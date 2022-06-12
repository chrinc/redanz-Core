package ch.redanz.redanzCore.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="country")
public class Country implements Serializable {

  @Id
  private Long id;

  @Column(name = "sort_name")
  private String sortName;

  @Column(name = "name")
  private String name;

  @JsonIgnore
  @Column(name = "phone_code")
  private Long phoneCode;

  @OneToMany
  @JoinColumn(name="person_id")
  @JsonIgnore
  private List<Person> person;

  public Country() {}

  public Country(Long id, String sortName, String name, Long phoneCode, List<Person> person) {
    this.id = id;
    this.sortName = sortName;
    this.name = name;
    this.phoneCode = phoneCode;
    this.person = person;
  }

  // Getters and Setters

  public List<Person> getPerson() {
    return person;
  }

  public void setPerson(List<Person> person) {
    this.person = person;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSortName() {
    return sortName;
  }

  public void setSortName(String sortName) {
    this.sortName = sortName;
  }

  public Long getPhoneCode() {
    return phoneCode;
  }

  public void setPhoneCode(Long phoneCode) {
    this.phoneCode = phoneCode;
  }


}



