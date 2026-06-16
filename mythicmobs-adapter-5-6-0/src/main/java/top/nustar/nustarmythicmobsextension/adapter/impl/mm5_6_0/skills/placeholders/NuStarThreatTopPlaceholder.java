/*
 *    NuStarMythicMobsExtension
 *    Copyright (C) 2025  NuStar
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills.placeholders;

import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.function.BiFunction;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.MetaPlaceholderAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.PlaceholderMetaAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.placeholders.NuStarThreatTopAdapter;
import top.nustar.nustarmythicmobsextension.service.PlaceholderService;
import top.nustar.nustarmythicmobsextension.service.annotations.MythicMobs5_6_0;
import top.nustar.nustarmythicmobsextension.service.enums.PlaceholderType;

/**
 * @author : NuStar Date : 2025/6/30 22:41 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
@Component
@MythicMobs5_6_0
@SuppressWarnings({"unused"})
public class NuStarThreatTopPlaceholder implements PlaceholderService {
    private final BiFunction<PlaceholderMeta, String, String> transformer = (meta, string) ->
            nuStarThreatTopAdapter.getTransformer().apply(new PlaceholderMetaAdapterImpl(meta), string);
    private static volatile NuStarThreatTopAdapter nuStarThreatTopAdapter;

    @Autowired
    public void setNuStarThreatTopAdapter(NuStarThreatTopAdapter nuStarThreatTopAdapter) {
        NuStarThreatTopPlaceholder.nuStarThreatTopAdapter = nuStarThreatTopAdapter;
    }

    @Override
    public PlaceholderAdapter<?> getPlaceholderAdapter() {
        return new MetaPlaceholderAdapterImpl(Placeholder.meta(transformer));
    }

    @Override
    public PlaceholderType getType() {
        return PlaceholderType.NUSTAR_THREAT_TOP;
    }
}
