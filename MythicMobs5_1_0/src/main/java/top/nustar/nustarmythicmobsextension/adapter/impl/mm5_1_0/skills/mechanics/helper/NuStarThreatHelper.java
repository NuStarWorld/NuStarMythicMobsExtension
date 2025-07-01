package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.skills.mechanics.helper;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.skills.mechanics.NuStarThreat;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs5_1_0;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;

/**
 * @author : NuStar
 * Date : 2025/6/25 23:17
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@MythicMobs5_1_0
@SuppressWarnings({"unused"})
public class NuStarThreatHelper implements MechanicHelperService {
    private volatile MobThreatManager mobThreatManager;

    @Autowired
    public void setMobThreatManager(MobThreatManager mobThreatManager) {
        this.mobThreatManager = mobThreatManager;
    }

    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new NuStarThreat("nustarthreat",(SkillExecutor) objects[0], (MythicLineConfig) objects[1], (MainConfiguration) objects[2], mobThreatManager);
    }

    @Override
    public MechanicType getType() {
        return MechanicType.NUSTAR_THREAT;
    }
}
