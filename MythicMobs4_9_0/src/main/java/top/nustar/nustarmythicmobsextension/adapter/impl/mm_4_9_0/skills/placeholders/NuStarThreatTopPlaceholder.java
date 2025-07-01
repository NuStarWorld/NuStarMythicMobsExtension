package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.placeholders;

import io.lumine.xikage.mythicmobs.skills.placeholders.Placeholder;
import io.lumine.xikage.mythicmobs.skills.placeholders.PlaceholderMeta;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.MetaPlaceholderAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.PlaceholderMetaAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.placeholders.NuStarThreatTopAdapter;
import top.nustar.nustarmythicmobsextension.service.PlaceholderService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs4_9_0;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportPlaceholderType;
import top.nustar.nustarmythicmobsextension.service.enums.PlaceholderType;

import java.util.function.BiFunction;

/**
 * @author : NuStar
 * Date : 2025/6/30 22:41
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@MythicMobs4_9_0
@SupportPlaceholderType(type = PlaceholderType.NUSTAR_THREAT_TOP)
@SuppressWarnings({"unused"})
public class NuStarThreatTopPlaceholder implements PlaceholderService {
    private final BiFunction<PlaceholderMeta, String, String> transformer = (meta, string) -> nuStarThreatTopAdapter.getTransformer().apply(new PlaceholderMetaAdapterImpl(meta), string);
    private volatile static NuStarThreatTopAdapter nuStarThreatTopAdapter;

    @Autowired
    public void setNuStarThreatTopAdapter(NuStarThreatTopAdapter nuStarThreatTopAdapter) {
        NuStarThreatTopPlaceholder.nuStarThreatTopAdapter = nuStarThreatTopAdapter;
    }

    @Override
    public PlaceholderAdapter<?> getPlaceholderAdapter() {
        return new MetaPlaceholderAdapterImpl(Placeholder.meta(transformer));
    }
}
