package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderManager;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderAdapter;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderManagerAdapter;

/**
 * @author : NuStar
 * Date : 2025/7/1 19:42
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class PlaceholderManagerAdapterImpl extends PlaceholderManagerAdapter<PlaceholderManager> {
    public PlaceholderManagerAdapterImpl(PlaceholderManager actualObject) {
        super(actualObject);
    }

    @Override
    public void register(String key, PlaceholderAdapter<?> transformer) {
        getActualObject().register(key, (Placeholder) transformer.getActualObject());
    }
}
