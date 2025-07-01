package top.nustar.nustarmythicmobsextension.service.enums;

import lombok.Getter;

/**
 * @author : NuStar
 * Date : 2025/6/30 22:35
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Getter
public enum PlaceholderType {
    NUSTAR_THREAT_TOP("nustar.threat.top");

    private final String name;

    PlaceholderType(String name) {
        this.name = name;
    }

    public static PlaceholderType of(String name) {
        for (PlaceholderType value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
