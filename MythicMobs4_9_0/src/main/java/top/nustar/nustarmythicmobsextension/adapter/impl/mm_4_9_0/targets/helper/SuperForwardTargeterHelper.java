package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets.helper;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets.SuperForwardTargeter;
import top.nustar.nustarmythicmobsextension.service.TargetSelectorHelperService;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportTargetSelectorType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;
import top.nustar.nustarmythicmobsextension.service.enums.TargetSelectorType;

/**
 * @author : NuStar
 * Date : 2025/6/20 22:13
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@SupportTargetSelectorType(type = TargetSelectorType.SUPER_FORWARD_TARGETER, version = MythicMobsVersion.MM4_9_0)
@SuppressWarnings({"unused"})
public class SuperForwardTargeterHelper implements TargetSelectorHelperService {
    @Override
    public NuStarTargerSelector findSelector(Object... objects) {
        return new SuperForwardTargeter((MythicLineConfig) objects[0]);
    }
}
