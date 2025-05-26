package top.nustar.nustarmythicmobsextension.service.enums;

import lombok.Getter;

@Getter
public enum MythicMobsVersion {
    MM4_9_0("4.9.0"),
    MM5_1_0("5.1.0"),
    MM5_6_0("5.6.0");

    private final String version;

    MythicMobsVersion(String version) {
        this.version = version;
    }

}
