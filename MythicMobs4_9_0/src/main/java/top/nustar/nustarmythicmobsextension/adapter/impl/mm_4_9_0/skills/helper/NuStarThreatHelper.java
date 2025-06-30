package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.NuStarThreat;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs4_9_0;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;

/**
 * @author : NuStar
 * Date : 2025/6/25 23:17
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@SupportMechanicType(type = MechanicType.NUSTAR_THREAT)
@MythicMobs4_9_0
@SuppressWarnings({"unused"})
public class NuStarThreatHelper implements MechanicHelperService {
    private volatile MobThreatManager mobThreatManager;

    @Autowired
    public void setMobThreatManager(MobThreatManager mobThreatManager) {
        this.mobThreatManager = mobThreatManager;
    }

    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new NuStarThreat("nustarthreat", (MythicLineConfig) objects[0], (MainConfiguration) objects[1], mobThreatManager);
    }
}
