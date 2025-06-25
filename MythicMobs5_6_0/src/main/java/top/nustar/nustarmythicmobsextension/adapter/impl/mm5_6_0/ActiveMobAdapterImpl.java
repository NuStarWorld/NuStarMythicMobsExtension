package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.core.mobs.ActiveMob;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;

import java.util.UUID;

/**
 * @author : NuStar
 * Date : 2025/6/24 20:09
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class ActiveMobAdapterImpl extends ActiveMobAdapter<ActiveMob> {
    public ActiveMobAdapterImpl(ActiveMob actualObject) {
        super(actualObject);
    }

    @Override
    public String getMobType() {
        return getActualObject().getMobType();
    }

    @Override
    public AbstractEntityAdapter<?> getParent() {
        return new AbstractEntityAdapterImpl(getActualObject().getParent().get());
    }

    @Override
    public UUID getOwner() {
        return getActualObject().getOwner().orElse(null);
    }

    @Override
    public void setLastDamageSkillAmount(double damage) {
        getActualObject().setLastDamageSkillAmount(damage);
    }
}
