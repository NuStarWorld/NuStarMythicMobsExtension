package top.nustar.nustarmythicmobsextension.adapter;

import java.util.UUID;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:08
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public abstract class ActiveMobAdapter<T> extends AbstractAdapter<T> {
    public ActiveMobAdapter(T actualObject) {
        super(actualObject);
    }

    public abstract String getMobType();

    public abstract AbstractEntityAdapter<?> getParent();

    public abstract UUID getOwner();

    public abstract void setLastDamageSkillAmount(double damage);
}
