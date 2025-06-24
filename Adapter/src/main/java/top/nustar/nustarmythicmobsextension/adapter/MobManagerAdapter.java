package top.nustar.nustarmythicmobsextension.adapter;

import org.bukkit.entity.Entity;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:04
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public abstract class MobManagerAdapter<T> extends AbstractAdapter<T> {
    public MobManagerAdapter(T actualObject) {
        super(actualObject);
    }

    public abstract ActiveMobAdapter<?> getMythicMobInstance(AbstractEntityAdapter<?> abstractEntityAdapter);

    public abstract ActiveMobAdapter<?> getMythicMobInstance(Entity entity);
}
