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
  // block of the overview report (see ReportStatsService)
  private String block;
  // track name of a track row (item is "bundle / track" or only the bundle)
  private String track;
  // cancelled registrations (count, split by dance role), shown in the details of the overview
  private List<String> countCancelled;

  // sold out override (only for items with a sold_out flag)
  private String soldOutItemType;
  private Long soldOutItemId;
  private Boolean soldOutOverride;
  private Boolean soldOutComputed;

  // sold out per dance role (tracks only)
  private List<ResponseStatsDanceRole> danceRoles;
  private Boolean partnerRegistrationPossible;
  // pass of a track row (sold out fields only)
  private ResponseStats bundle;

  public ResponseStats(String type, String item, List<String> countAll, List<String> countSubmitted, List<String> countConfirming, List<String> countDone, Integer maxCount, String soldOut) {
    this(type, item, countAll, countSubmitted, countConfirming, countDone, maxCount, soldOut, null, null, null, null, null, null, null, null, null, null);
  }

  public ResponseStats inBlock(String block) {
    this.block = block;
    return this;
  }

  public ResponseStats withCountCancelled(List<String> countCancelled) {
    this.countCancelled = countCancelled;
    return this;
  }

  public ResponseStats withSoldOutOverride(String soldOutItemType, Long soldOutItemId, Boolean soldOutOverride, Boolean soldOutComputed) {
    this.soldOutItemType = soldOutItemType;
    this.soldOutItemId = soldOutItemId;
    this.soldOutOverride = soldOutOverride;
    this.soldOutComputed = soldOutComputed;
    return this;
  }

  public ResponseStats withBundle(ResponseStats bundle) {
    this.bundle = bundle;
    return this;
  }

  public ResponseStats withDanceRoles(List<ResponseStatsDanceRole> danceRoles, boolean partnerRegistrationPossible) {
    this.danceRoles = danceRoles;
    this.partnerRegistrationPossible = partnerRegistrationPossible;
    return this;
  }
}
