package ch.redanz.redanzCore.model.reporting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseStatsDanceRole {
  private Long danceRoleId;
  private String danceRoleName;
  private Long soldOutItemId;
  // OPEN, PARTNER_ONLY, SOLD_OUT; mode null = computed
  private String soldOutMode;
  private String soldOutComputed;
  private String soldOutStatus;
}
