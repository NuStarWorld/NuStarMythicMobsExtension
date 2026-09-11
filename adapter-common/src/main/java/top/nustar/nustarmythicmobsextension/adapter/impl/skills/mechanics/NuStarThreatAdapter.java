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

package top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.calculate.Expression;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.GlobalVariable;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;

/**
 * @author : NuStar Date : 2025/6/25 21:32 Website : <a href="https://www.nustar.top">nustar's web</a> Github : <a
 *     href="https://github.com/nustarworld">nustar's github</a> QQ : 3318029085
 */
public class NuStarThreatAdapter implements NuStarSkill, GlobalVariable {
    protected final String mode;
    protected final Expression amount;
    protected final PlaceholderDoubleAdapter<?> multiple;
    protected final MobThreatService mobThreatService;
    protected final MainConfiguration mainConfiguration;

    public NuStarThreatAdapter(
            PlaceholderDoubleHelper<?> placeholderDoubleHelper,
            MythicLineConfigAdapter<?> mlc,
            MainConfiguration mainConfiguration,
            MobThreatService mobThreatService) {
        this.mode = mlc.getString(new String[] {"mode", "m"}, "add");
        this.amount = new Expression(mlc.getString(new String[] {"amount", "a"}, "0")).compile();
        this.multiple = placeholderDoubleHelper.of(mlc.getString(new String[] {"multiple", "m"}, "1"));
        this.mobThreatService = mobThreatService;
        this.mainConfiguration = mainConfiguration;
    }

    @Override
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        // 威胁记录以施法者为条目，施法者不是生物时无法记分。
        LivingEntity caster = NuStarSkill.livingCaster(skillMetadata);
        if (caster == null) {
            return false;
        }
        // 展示实体等非生物目标既不会仇恨也不会被转移，跳过它们而不中断整个技能。
        LivingEntity entity = NuStarSkill.livingTarget(abstractEntity);
        if (entity == null) {
            return false;
        }
        long amount = this.amount
                .calculate(parseExpressionContext(
                        skillMetadata,
                        abstractEntity,
                        mainConfiguration.getVariables().values()))
                .longValue();
        if (!mode.equals("transfer") && !(entity instanceof Creature)) return false;
        switch (mode) {
            case "add":
                mobThreatService.addThreat((Creature) entity, caster.getUniqueId(), amount);
                break;
            case "set":
                mobThreatService.setThreat((Creature) entity, caster.getUniqueId(), amount);
                break;
            case "delete":
                mobThreatService.deleteThreat((Creature) entity, caster.getUniqueId());
                break;
            case "top":
                mobThreatService.topThreat((Creature) entity, caster.getUniqueId());
                break;
            case "transfer":
                mobThreatService.transferMobThreat(caster.getUniqueId(), entity);
                break;
        }
        return true;
    }
}
