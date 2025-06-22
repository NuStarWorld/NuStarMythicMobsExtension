package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills.helper;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.DependsOn;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills.FastAP;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs5_6_0;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;

/**
 * @author : NuStar
 * Date : 2025/6/22 21:49
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@SupportMechanicType(type = MechanicType.FAST_AP)
@MythicMobs5_6_0
@DependsOn(classes = "io.lumine.mythic.core.config.MythicConfigImpl")
@SuppressWarnings({"unused"})
public class FastAPHelper implements MechanicHelperService {
    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new FastAP("fastap",(SkillExecutor)objects[0], (MythicLineConfig) objects[1], (MainConfiguration) objects[2]);
    }
}
