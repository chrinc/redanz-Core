package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseStats {
  private String type;
  private String item;
  private int count;
  private Integer maxCount;
}
