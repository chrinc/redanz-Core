package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleConfig {
  EXTRA_FUN_PASS("Extra FUN Pass", 330, OutTextConfig.LABEL_EXTRAFUN_DESC_EN.getOutTextKey(), 120, false, "extra_fun_pass", 1, null),
  FUN_PASS("FUN Pass", 250, OutTextConfig.LABEL_FUN_DESC_EN.getOutTextKey(), 90, false, "fun_pass", 2, null),
  PARTYPASS("Party Pass", 100, OutTextConfig.LABEL_PARTYPASS_DESC_EN.getOutTextKey(), 49, false, "party_pass", 4, null),
  PARTY_SUN("Sunday Party Ticket", 40, OutTextConfig.LABEL_PARTY_SUN_DESC_EN.getOutTextKey(), 30, true, "party_sunday",5, List.of(SlotConfig.SLOT_SUNDAY)),
  FRIDAY_SPECIAL("Friday Special", 90, OutTextConfig.LABEL_FRIDAY_PACKAGE_DESC_EN.getOutTextKey(), 20, false, "friday_special",3, null)
  ;

  private final String name;
  private final Integer price;
  private final String description;
  private final Integer capacity;
  private final Boolean simpleTicket;
  private final String internalId;
  private final Integer seqNr;
  private final List<SlotConfig> partySlots;

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
            bundleConfig.getInternalId(),
            bundleConfig.getSeqNr(),
            partySlots
          )
        );
      } else {
        Bundle bundle = bundleService.findByName(bundleConfig.name);
        bundle.setCapacity(bundleConfig.capacity);
        bundle.setDescription(bundleConfig.getDescription());
        bundle.setPrice(bundleConfig.getPrice());
        bundle.setSimpleTicket(bundleConfig.simpleTicket);
        bundle.setInternalId(bundleConfig.internalId);
        bundle.setSeqNr(bundleConfig.seqNr);
        bundle.setPartySlots(partySlots);
        bundleService.save(bundle);
      }
    }
  }
}
