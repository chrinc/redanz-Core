package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.SlotConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundlePartySlotConfig {
  FULL_FRI(BundleConfig.FULL_PASS, SlotConfig.SLOT_FRIDAY),
  FULL_SAT(BundleConfig.FULL_PASS, SlotConfig.SLOT_SATURDAY),
  FULL_SUN(BundleConfig.FULL_PASS, SlotConfig.SLOT_SUNDAY),

  HALF_FRI(BundleConfig.HALF_PASS, SlotConfig.SLOT_FRIDAY),
  HALF_SAT(BundleConfig.HALF_PASS, SlotConfig.SLOT_SATURDAY),
  HALF_SUN(BundleConfig.HALF_PASS, SlotConfig.SLOT_SUNDAY),

  PARTY_FRI(BundleConfig.PARTY_PASS, SlotConfig.SLOT_FRIDAY),
  PARTY_SAT(BundleConfig.PARTY_PASS, SlotConfig.SLOT_SATURDAY),
  PARTY_SUN(BundleConfig.PARTY_PASS, SlotConfig.SLOT_SUNDAY),;

  private final BundleConfig bundleConfig;
  private final SlotConfig slotConfig;
  public static void setup(SlotService slotService, BundleService bundleService) {
    for (BundlePartySlotConfig bundlePartySlotConfig : BundlePartySlotConfig.values()) {
      Bundle bundle = bundleService.findByName(bundlePartySlotConfig.getBundleConfig().getName());
      Slot slot = slotService.findByName(bundlePartySlotConfig.getSlotConfig().getName());
      bundle.getPartySlots().add(slot);
      bundleService.save(bundle);
    }
  }
}
