package top.nustar.nustarmythicmobsextension.adapter.impl.skills;

import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

/**
 * @author : NuStar
 * Date : 2025/6/25 21:32
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public interface NuStarSkill {
    boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity);
}
