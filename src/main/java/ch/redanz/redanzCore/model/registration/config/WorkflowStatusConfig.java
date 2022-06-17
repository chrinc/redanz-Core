package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum WorkflowStatusConfig {

    OPEN("Open", OutTextConfig.LABEL_WORKFLOW_OPEN_EN.getOutTextKey()),
    SUBMITTED("Submitted", OutTextConfig.LABEL_WORKFLOW_SUBMITTED_EN.getOutTextKey()),
    CONFIRMING("Confirming", OutTextConfig.LABEL_WORKFLOW_CONFIRMING_EN.getOutTextKey()),
    DONE("Done", OutTextConfig.LABEL_WORKFLOW_DONE_EN.getOutTextKey()),
    CANCELLED("Cancelled", OutTextConfig.LABEL_WORKFLOW_CANCELLED_EN.getOutTextKey());

    private final String name;
    private final String label;
    public String getName() {
        return name;
    }

    public static List<WorkflowStatus> setup() {
        List<WorkflowStatus> transitionList = new ArrayList<>();

        for (WorkflowStatusConfig workflowStatusConfig : WorkflowStatusConfig.values()) {
            transitionList.add(
                    new WorkflowStatus(
                      workflowStatusConfig.getName(),
                      workflowStatusConfig.getLabel()
                    )
            );
        }
        return transitionList;
    }


}
