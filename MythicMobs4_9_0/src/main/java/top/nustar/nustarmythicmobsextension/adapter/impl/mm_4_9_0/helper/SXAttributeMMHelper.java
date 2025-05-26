package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.SXAttributeMM;
import top.nustar.nustarmythicmobsextension.service.MechanicHelperService;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;

@Component
@SupportMechanicType(type = MechanicType.SX_ATTRIBUTE_MM, version = MythicMobsVersion.MM4_9_0)
@SuppressWarnings({"unused"})
public class SXAttributeMMHelper implements MechanicHelperService {
    @Override
    public NuStarMechanic findMechanic(Object... objects) {
        return new SXAttributeMM("sxmm", (MythicLineConfig) objects[0]);
    }
}
