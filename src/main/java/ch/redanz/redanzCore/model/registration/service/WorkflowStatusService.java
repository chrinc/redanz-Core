package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.repository.WorkflowStatusRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class WorkflowStatusService {
  private final WorkflowStatusRepo workflowStatusRepo;

  public void save(WorkflowStatus workflowStatus) {
    workflowStatusRepo.save(workflowStatus);
  }

  public WorkflowStatus findByWorkflowStatusName(String name) {
    return workflowStatusRepo.findByName(name);
  }

  public WorkflowStatus getSubmitted() {
    return findByWorkflowStatusName(WorkflowStatusConfig.SUBMITTED.getName());
  }

  public WorkflowStatus getCancelled() {
    return findByWorkflowStatusName(WorkflowStatusConfig.CANCELLED.getName());
  }

  public WorkflowStatus getDone() {
    return findByWorkflowStatusName(WorkflowStatusConfig.DONE.getName());
  }

  public WorkflowStatus getConfirming() {
    return findByWorkflowStatusName(WorkflowStatusConfig.CONFIRMING.getName());
  }

  public List<WorkflowStatus> findAll() {
    return workflowStatusRepo.findAll();
  }

  public List<WorkflowStatus> findAllOpen() {
    List<WorkflowStatus> openWorkflowStatusList = new ArrayList<>();
    openWorkflowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.OPEN.getName()));
    openWorkflowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.SUBMITTED.getName()));
    openWorkflowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.CONFIRMING.getName()));
    return openWorkflowStatusList;
  }

  public List<WorkflowStatus> findAllDone() {
    List<WorkflowStatus> doneWorkfowStatusList = new ArrayList<>();
    doneWorkfowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.DONE.getName()));
    return doneWorkfowStatusList;
  }

  public List<WorkflowStatus> findAllConfirming() {
    List<WorkflowStatus> confirmingWorkflowStatusList = new ArrayList<>();
    confirmingWorkflowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.CONFIRMING.getName()));
    return confirmingWorkflowStatusList;
  }

  public List<WorkflowStatus> findAllSubmitted() {
    List<WorkflowStatus> confirmingWorkflowStatusList = new ArrayList<>();
    confirmingWorkflowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.SUBMITTED.getName()));
    return confirmingWorkflowStatusList;
  }
}
