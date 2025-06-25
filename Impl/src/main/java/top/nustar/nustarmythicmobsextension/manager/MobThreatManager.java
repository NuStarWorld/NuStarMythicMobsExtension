package top.nustar.nustarmythicmobsextension.manager;

import org.bukkit.entity.Creature;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
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

public class MobThreatManager {
    private final Map<UUID, MobThreat> mobThreatMap = new ConcurrentHashMap<>();

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
}
