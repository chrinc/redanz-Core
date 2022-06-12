package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum WorkflowStatusConfig {

    OPEN("Open"),
    SUBMITTED("Submitted"),
    CONFIRMING("Confirming"),
    DONE("Done"),
    CANCELLED("Cancelled");

    private final String name;
    public String getName() {
        return name;
    }

    public static List<WorkflowStatus> setup() {
        List<WorkflowStatus> transitionList = new ArrayList<>();

        for (WorkflowStatusConfig workflowStatusConfig : WorkflowStatusConfig.values()) {
            transitionList.add(
                    new WorkflowStatus(workflowStatusConfig.getName())
            );
        }
        return transitionList;
    }


}
