package top.nustar.nustarmythicmobsextension.adapter.impl.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : NuStar
 * Date : 2025/6/25 22:53
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public interface GlobalVariable {
    default Map<String, Number> parseExpressionContext(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity, List<Variable> variables) {
        LivingEntity caster = (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        Map<String, Number> context = new HashMap<>(variables.size());
        if (caster instanceof Player) {
            for (Variable variable : variables) {
                Player player = (Player) caster;
                context.put(variable.getName(), variable.asBigDecimal(player));
            }
        }
        context.put("caster_level", skillMetadata.getCaster().getLevel());
        context.put("caster_hp", skillMetadata.getCaster().getEntity().getHealth());
        context.put("caster_mhp", skillMetadata.getCaster().getEntity().getMaxHealth());
        context.put("target_hp", abstractEntity.getHealth());
        context.put("target_mhp", abstractEntity.getMaxHealth());
        return context;
    }
}
