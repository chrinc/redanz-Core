package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.WorkflowStatus;
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

    public WorkflowStatus findByWorkflowStatusName(String name) {
        return workflowStatusRepo.findByName(name);
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
    public List<WorkflowStatus> findAllCancelled() {
        List<WorkflowStatus> cancelledWorkflowStatusList = new ArrayList<>();
        cancelledWorkflowStatusList.add(workflowStatusRepo.findByName(WorkflowStatusConfig.CANCELLED.getName()));
        return cancelledWorkflowStatusList;
    }

    public WorkflowStatus findNextWorkflowStatus(WorkflowStatus workflowStatus) {
        if(workflowStatus.getName().equals(WorkflowStatusConfig.OPEN.getName()))
            return workflowStatusRepo.findByName(WorkflowStatusConfig.SUBMITTED.getName());
        if(workflowStatus.getName().equals(WorkflowStatusConfig.SUBMITTED.getName()))
            return workflowStatusRepo.findByName(WorkflowStatusConfig.CONFIRMING.getName());
        if(workflowStatus.getName().equals(WorkflowStatusConfig.CONFIRMING.getName()))
            return workflowStatusRepo.findByName(WorkflowStatusConfig.DONE.getName());
        else return null;
    }
}
