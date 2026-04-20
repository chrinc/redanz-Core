package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.EventSlotConfig;
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
  FULL_FRI(BundleConfig.FULL_PASS, EventSlotConfig.SLOT_FRI_SAT),
  FULL_SAT(BundleConfig.FULL_PASS, EventSlotConfig.SLOT_SAT_SUN),
  FULL_SUN(BundleConfig.FULL_PASS, EventSlotConfig.SLOT_SUN_MON),

  HALF_FRI(BundleConfig.HALF_PASS, EventSlotConfig.SLOT_FRI_SAT),
  HALF_SAT(BundleConfig.HALF_PASS, EventSlotConfig.SLOT_SAT_SUN),
  HALF_SUN(BundleConfig.HALF_PASS, EventSlotConfig.SLOT_SUN_MON),

  PARTY_FRI(BundleConfig.PARTY_PASS, EventSlotConfig.SLOT_FRIDAY),
  PARTY_SAT(BundleConfig.PARTY_PASS, EventSlotConfig.SLOT_SATURDAY),
  PARTY_SUN(BundleConfig.PARTY_PASS, EventSlotConfig.SLOT_SUNDAY)
  ;

  private final BundleConfig bundleConfig;
  private final EventSlotConfig eventSlotConfig;
  public static void setup(SlotService slotService, BundleService bundleService) {
    for (BundlePartySlotConfig bundlePartySlotConfig : BundlePartySlotConfig.values()) {
      Bundle bundle = bundleService.findByName(bundlePartySlotConfig.getBundleConfig().getName());
      EventSlot eventSlot = slotService.findEventSlotByName(bundlePartySlotConfig.getEventSlotConfig().getName());
      bundle.getPartySlots().add(eventSlot);
      bundleService.save(bundle);
    }
  }
}
