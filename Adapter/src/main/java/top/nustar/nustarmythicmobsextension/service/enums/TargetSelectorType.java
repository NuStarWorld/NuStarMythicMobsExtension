package top.nustar.nustarmythicmobsextension.service.enums;


public enum TargetSelectorType {
    ENTITIES_IN_TARGETS("EntitiesInTargets");

    private final String name;

    TargetSelectorType(String name) {
        this.name = name;
    }

    public static TargetSelectorType of(String name) {
        for (TargetSelectorType value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
