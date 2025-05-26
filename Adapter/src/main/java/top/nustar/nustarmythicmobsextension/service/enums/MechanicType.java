package top.nustar.nustarmythicmobsextension.service.enums;

public enum MechanicType {
    ATTRIBUTE_PLUS_MM("apmm"),
    ATTRIBUTE_PLUS_SOURCE("apsource"),
    SX_ATTRIBUTE_MM("sxmm");

    private final String type;

    MechanicType(String type) {
        this.type = type;
    }

    public static MechanicType of(String name) {
        for (MechanicType value : values()) {
            if (value.type.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
