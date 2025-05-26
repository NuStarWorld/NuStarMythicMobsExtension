package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.helper;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.skills.AttributePlusSource;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;

@Component
@SupportMechanicType(type = MechanicType.ATTRIBUTE_PLUS_SOURCE, version = MythicMobsVersion.MM5_1_0)
@SuppressWarnings({"unused"})
public class AttributePlusSourceHelper implements MechanicHelperService {
    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new AttributePlusSource("apsource", (SkillExecutor)objects[0], (MythicLineConfig) objects[1]);
    }

}
