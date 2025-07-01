package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderMetaAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillCasterAdapter;

/**
 * @author : NuStar
 * Date : 2025/7/1 00:30
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class PlaceholderMetaAdapterImpl extends PlaceholderMetaAdapter<PlaceholderMeta> {
    public PlaceholderMetaAdapterImpl(PlaceholderMeta actualObject) {
        super(actualObject);
    }

    @Override
    public SkillCasterAdapter<?> getCaster() {
        return new SkillCasterAdapterImpl(getActualObject().getCaster());
    }

    @Override
    public AbstractEntityAdapter<?> getTrigger() {
        return new AbstractEntityAdapterImpl(getActualObject().getTrigger());
    }
}
