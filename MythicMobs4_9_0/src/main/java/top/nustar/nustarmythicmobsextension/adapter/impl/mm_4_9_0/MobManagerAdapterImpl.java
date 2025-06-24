package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.entity.Entity;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MobManagerAdapter;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:07
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class MobManagerAdapterImpl extends MobManagerAdapter<MobManager> {
    public MobManagerAdapterImpl(MobManager actualObject) {
        super(actualObject);
    }

    @Override
    public ActiveMobAdapter<?> getMythicMobInstance(AbstractEntityAdapter<?> abstractEntityAdapter) {
        return new ActiveMobAdapterImpl(getActualObject().getMythicMobInstance((AbstractEntity) abstractEntityAdapter.getActualObject()));
    }

    @Override
    public ActiveMobAdapter<?> getMythicMobInstance(Entity entity) {
        return getMythicMobInstance(new AbstractEntityAdapterImpl(BukkitAdapter.adapt(entity)));
    }
}
