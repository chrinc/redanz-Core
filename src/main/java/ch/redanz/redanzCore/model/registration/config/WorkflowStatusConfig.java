package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Slf4j
public enum WorkflowStatusConfig {

  OPEN("Open", OutTextConfig.LABEL_WORKFLOW_OPEN_EN.getOutTextKey()),
  SUBMITTED("Submitted", OutTextConfig.LABEL_WORKFLOW_SUBMITTED_EN.getOutTextKey()),
  CONFIRMING("Confirming", OutTextConfig.LABEL_WORKFLOW_CONFIRMING_EN.getOutTextKey()),
  DONE("Done", OutTextConfig.LABEL_WORKFLOW_DONE_EN.getOutTextKey()),
  CANCELLED("Cancelled", OutTextConfig.LABEL_WORKFLOW_CANCELLED_EN.getOutTextKey()),
  DELETED("Deleted", OutTextConfig.LABEL_WORKFLOW_DELETED_EN.getOutTextKey());

  private final String name;
  private final String label;

  public String getName() {
    return name;
  }

  public static void setup(WorkflowStatusService workflowStatusService) {
    for (WorkflowStatusConfig workflowStatusConfig : WorkflowStatusConfig.values()) {
       if (!workflowStatusService.existsByName(workflowStatusConfig.getName())) {
           workflowStatusService.save(
             new WorkflowStatus(
               workflowStatusConfig.getName(),
               workflowStatusConfig.getLabel()
             )
           );
       } else {
           WorkflowStatus workflowStatus = workflowStatusService.findByWorkflowStatusName(workflowStatusConfig.getName());
           workflowStatus.setLabel(workflowStatusConfig.label);
           workflowStatusService.save(workflowStatus);
       }
    }
  }


}
