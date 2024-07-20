package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseStats {
  private String type;
  private String item;
  private List<String> countAll;
  private List<String> countSubmitted;
  private List<String> countConfirming;
  private List<String> countDone;
  private Integer maxCount;
  private String soldOut;
//  private String itemAttribute;
}
