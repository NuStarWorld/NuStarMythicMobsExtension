package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.core.mobs.MobExecutor;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.adapter.MobManagerAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderManagerAdapter;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs5_6_0;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:13
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@MythicMobs5_6_0
@SuppressWarnings("unused")
public class MythicInstanceImpl implements MythicInstance {
    private final MythicPlugin instance;

    public MythicInstanceImpl() {
        this.instance = MythicProvider.get();
    }

    @Override
    public MobManagerAdapter<?> getMobManager() {
        return new MobManagerAdapterImpl((MobExecutor) instance.getMobManager());
    }

    @Override
    public PlaceholderManagerAdapter<?> getPlaceholderManager() {
        return new PlaceholderManagerAdapterImpl(instance.getPlaceholderManager());
    }
}
