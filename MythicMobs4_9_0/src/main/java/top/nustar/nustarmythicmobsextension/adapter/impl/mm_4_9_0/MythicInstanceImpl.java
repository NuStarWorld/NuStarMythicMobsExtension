package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.MythicMobs;
import team.idealstate.sugar.next.context.annotation.component.Component;
import top.nustar.nustarmythicmobsextension.adapter.MobManagerAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs4_9_0;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:13
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@SuppressWarnings("unused")
@MythicMobs4_9_0
public class MythicInstanceImpl implements MythicInstance {
    private final MythicMobs instance;

    public MythicInstanceImpl() {
        this.instance = MythicMobs.inst();
    }

    @Override
    public MobManagerAdapter<?> getMobManager() {
        return new MobManagerAdapterImpl(instance.getMobManager());
    }
}
