package top.nustar.nustarmythicmobsextension.service;

import top.nustar.nustarmythicmobsextension.adapter.PlaceholderAdapter;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportPlaceholderType;
import top.nustar.nustarmythicmobsextension.service.enums.PlaceholderType;

/**
 * @author : NuStar
 * Date : 2025/6/30 22:46
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public interface PlaceholderService {
    PlaceholderAdapter<?> getPlaceholderAdapter();
    default PlaceholderType findPlaceholderTypeFromService(PlaceholderService placeholderService) {
        SupportPlaceholderType supportPlaceholderType = placeholderService.getClass().getAnnotation(SupportPlaceholderType.class);
        return supportPlaceholderType.type();
    }
}
