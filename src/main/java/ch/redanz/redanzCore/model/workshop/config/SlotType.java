package ch.redanz.redanzCore.model.workshop.config;

public enum SlotType {
    FOOD(10, "LABEL-SLOT-TYPE-FOOD"),
    PARTY(20, "LABEL-SLOT-TYPE-PARTY"),
    VOLUNTEER(30, "LABEL-SLOT-TYPE-VOLUNTEER"),
    ACCOMMODATION(40, "LABEL-SLOT-TYPE-ACCOMMODATION");

    private final int code;
    private final String label;

    SlotType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static SlotType typeByCode(int code) {
        return java.util.Arrays.stream(values())
          .filter(type -> type.code == code)
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Unknown SlotType code: " + code));
    }
    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
