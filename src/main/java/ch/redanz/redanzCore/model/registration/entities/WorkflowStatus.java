package ch.redanz.redanzCore.model.registration.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "workflow_status")
@Getter
@Setter
public class WorkflowStatus implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "workflow_status_id")
  private Long workflowStatusId;

  private String name;

  private String label;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowStatus")
  @JsonIgnore
//    @Expose(serialize = false, deserialize = false)
  private List<WorkflowTransition> transitionList;

  public WorkflowStatus() {
  }

  public WorkflowStatus(String name, String label) {
    this.name = name;
    this.label = label;
  }

}

