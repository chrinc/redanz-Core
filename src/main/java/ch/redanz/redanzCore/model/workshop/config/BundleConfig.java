package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleConfig {
  FULL_PASS("Full Pass", 330, OutTextConfig.LABEL_FULLPASS_DESC_EN.getOutText(), 120, false, 1, null, "#ffaa4d"),
  HALF_PASS("Half Pass", 250, OutTextConfig.LABEL_HALFPASS_DESC_EN.getOutTextKey(), 90, false, 2, null, "#FF3EB5"),
  PARTYPASS("Party Pass", 100, OutTextConfig.LABEL_PARTYPASS_DESC_EN.getOutTextKey(), 50, false, 4, null, "#9DE7D7")
  ;

  private final String name;
  private final Integer price;
  private final String description;
  private final Integer capacity;
  private final Boolean simpleTicket;
  private final Integer seqNr;
  private final List<SlotConfig> partySlots;
  private final String color;

  public static void setup(BundleService bundleService, SlotService slotService) {
    List<Slot> partySlots = new ArrayList<>();

    for (BundleConfig bundleConfig : BundleConfig.values()) {
      partySlots.clear();

      if (bundleConfig.partySlots != null) {
        bundleConfig.partySlots.forEach(slotConfig -> {
          partySlots.add(slotService.findByName(slotConfig.getName()));
        });
      };

      if (!bundleService.existsByName(bundleConfig.getName())) {

        bundleService.save(
          new Bundle(
            bundleConfig.getName(),
            bundleConfig.getPrice(),
            bundleConfig.getDescription(),
            bundleConfig.getCapacity(),
            bundleConfig.getSimpleTicket(),
            bundleConfig.getSeqNr(),
            partySlots,
            bundleConfig.color
          )
        );
      } else {
        Bundle bundle = bundleService.findByName(bundleConfig.name);
        bundle.setCapacity(bundleConfig.capacity);
        bundle.setDescription(bundleConfig.getDescription());
        bundle.setPrice(bundleConfig.getPrice());
        bundle.setSimpleTicket(bundleConfig.simpleTicket);
        bundle.setSeqNr(bundleConfig.seqNr);
        bundle.setPartySlots(partySlots);
        bundle.setColor(bundleConfig.color);
        bundleService.save(bundle);
      }
    }
  }
}
