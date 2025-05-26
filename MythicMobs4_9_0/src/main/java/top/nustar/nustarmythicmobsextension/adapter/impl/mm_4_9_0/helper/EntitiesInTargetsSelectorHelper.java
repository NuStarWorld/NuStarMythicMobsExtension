package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets.EntitiesInTargetsSelector;
import top.nustar.nustarmythicmobsextension.service.TargetSelectorHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportTargetSelectorType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;
import top.nustar.nustarmythicmobsextension.service.enums.TargetSelectorType;

@Component
@SupportTargetSelectorType(type = TargetSelectorType.ENTITIES_IN_TARGETS, version = MythicMobsVersion.MM4_9_0)
@SuppressWarnings({"unused"})
public class EntitiesInTargetsSelectorHelper implements TargetSelectorHelperService {

    @Override
    public NuStarTargerSelector findSelector(Object... objects) {
        return new EntitiesInTargetsSelector((MythicLineConfig) objects[0]);
    }
}
