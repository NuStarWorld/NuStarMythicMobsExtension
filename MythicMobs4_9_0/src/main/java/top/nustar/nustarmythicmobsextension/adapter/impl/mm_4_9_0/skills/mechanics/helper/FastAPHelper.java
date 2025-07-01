package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.mechanics.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.mechanics.FastAP;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs4_9_0;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;

/**
 * @author : NuStar
 * Date : 2025/6/22 21:49
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@MythicMobs4_9_0
@SuppressWarnings({"unused"})
public class FastAPHelper implements MechanicHelperService {
    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new FastAP("fastap", (MythicLineConfig) objects[0], (MainConfiguration) objects[1]);
    }

    @Override
    public MechanicType getType() {
        return MechanicType.FAST_AP;
    }
}
