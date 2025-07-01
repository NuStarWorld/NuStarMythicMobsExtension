package top.nustar.nustarmythicmobsextension.adapter.impl.skills.placeholders;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderMetaAdapter;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;
import top.nustar.nustarmythicmobsextension.utils.InstanceUtil;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

/**
 * @author : NuStar
 * Date : 2025/7/1 00:27
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
public class NuStarThreatTopAdapter {
    @Getter
    private final BiFunction<PlaceholderMetaAdapter<?>, String, String> transformer;
    private volatile MobThreatManager mobThreatManager;

    @Autowired
    public void setMobThreatManager(MobThreatManager mobThreatManager) {
        this.mobThreatManager = mobThreatManager;
    }

    public NuStarThreatTopAdapter() {
        this.transformer = ((placeholderMetaAdapter, string) -> {
            LivingEntity trigger = (LivingEntity) placeholderMetaAdapter.getTrigger().getBukkitEntity();
            if (!(trigger instanceof Creature)) return "该怪物不具备威胁度功能";
            Creature mob = (Creature) trigger;
            UUID top = mobThreatManager.getMobThreat(mob).getTopThreat();
            if (top != null) {
                Entity topEntity;
                try {
                    topEntity = Bukkit.getScheduler().callSyncMethod(InstanceUtil.getInstance(Plugin.class), () -> Bukkit.getEntity(top)).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                if (topEntity == null) return "无目标";
                return topEntity.getName();
            }
            if (mob.getTarget() != null) {
                return mob.getTarget().getName();
            }
            return "无目标";
        });
    }
}
