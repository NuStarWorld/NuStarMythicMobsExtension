package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;

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
}
