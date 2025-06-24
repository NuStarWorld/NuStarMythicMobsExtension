package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.core.mobs.MobExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.DependsOn;
import top.nustar.nustarmythicmobsextension.adapter.MobManagerAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:13
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@SuppressWarnings("unused")
@DependsOn(classes = "io.lumine.mythic.core.config.MythicConfigImpl")
public class MythicInstanceImpl implements MythicInstance {
    private final MythicPlugin instance;

    public MythicInstanceImpl() {
        this.instance = MythicProvider.get();
    }

    @Override
    public MobManagerAdapter<?> getMobManager() {
        return new MobManagerAdapterImpl((MobExecutor) instance.getMobManager());
    }
}
