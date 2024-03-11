package ch.redanz.redanzCore.model.reporting.response;

import ch.redanz.redanzCore.model.workshop.entities.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseCheckIn {
  private Long checkInId;
  private String name;
  private String bundle;
  private String track;
  private Slot slot;
  private String food;
  private String addons;
  private String discounts;
  private String privates;
  private String workflowStatus;
  private Long amountDue;
  private Long totalAmount;
  private String color;
  private ZonedDateTime checkInTime;

  public static List<Map<String, String>> schema() {

    return new ArrayList<>() {
      {
//        add(new HashMap<>() {{
//          put("key", "checkInId");
//          put("type", "id");
//          put("label", "Check In Id");
//        }});

        add(new HashMap<>() {{
          put("key", "name");
          put("type", "text");
          put("label", "Name");
        }});
        add(new HashMap<>() {{
          put("key", "color");
          put("type", "color");
          put("label", "Wrist Band Color");
        }});
        add(new HashMap<>() {{
          put("key", "bundle");
          put("type", "text");
          put("label", "Bundle / Guest");
        }});
        add(new HashMap<>() {{
          put("key", "track");
          put("type", "text");
          put("label", "Track / Description");
        }});

        add(new HashMap<>() {{
          put("key", "food");
          put("type", "text");
          put("label", "Food");
        }});

        add(new HashMap<>() {{
          put("key", "addons");
          put("type", "text");
          put("label", "Addons");
        }});

        add(new HashMap<>() {{
          put("key", "privates");
          put("type", "text");
          put("label", "Privates");
        }});

//        add(new HashMap<>() {{
//          put("key", "slot");
//          put("type", "text");
//          put("label", "Slot");
//        }});

        add(new HashMap<>() {{
          put("key", "discounts");
          put("type", "text");
          put("label", "Discounts");
        }});

        add(new HashMap<>() {{
          put("key", "workflowStatus");
          put("type", "text");
          put("label", "Status");
        }});

        add(new HashMap<>() {{
          put("key", "amountDue");
          put("type", "number");
          put("label", "Due");
        }});
        add(new HashMap<>() {{
          put("key", "totalAmount");
          put("type", "number");
          put("label", "Total Amount");
        }});

        add(new HashMap<>() {{
          put("key", "isUpdate");
          put("type", "isUpdate");
          put("label", "Check In");
        }});
      }
    };
  }
}
