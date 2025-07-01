package top.nustar.nustarmythicmobsextension.manager;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.entity.MobThreat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author : NuStar
 * Date : 2025/6/24 21:09
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings({"unused"})
public class MobThreatManager {
    private volatile MythicInstance mythicInstance;
    private final Map<UUID, MobThreat> mobThreatMap = new ConcurrentHashMap<>();

    @Autowired
    public void setMythicInstance(MythicInstance mythicInstance) {
        this.mythicInstance = mythicInstance;
    }

    public MobThreat getMobThreat(Creature mob) {
        return mobThreatMap.computeIfAbsent(mob.getUniqueId(), uuid -> new MobThreat(mob));
    }

    public void updateMobThreat(Creature mob, Consumer<MobThreat> consumer) {
        MobThreat mobThreat = getMobThreat(mob);
        consumer.accept(mobThreat);
    }

    public void removeMobThreat(Creature mob) {
        mobThreatMap.remove(mob.getUniqueId());
    }

    public void transferMobThreat(UUID from, LivingEntity target) {
        if (mythicInstance.getMobManager().getMythicMobInstance(target).getActualObject() != null) return;
        mobThreatMap.values().forEach(mobThreat -> mobThreat.transferThreat(from, target.getUniqueId()));
    }
    public void clearThreat(UUID uuid) {
        mobThreatMap.values().forEach(mobThreat -> mobThreat.removeEntityThreat(uuid));
    }
}
