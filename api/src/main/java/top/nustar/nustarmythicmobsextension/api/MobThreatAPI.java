package top.nustar.nustarmythicmobsextension.api;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : NuStar
 * Date : 2025/8/3 14:03
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@SuppressWarnings("unused")
public class MobThreatAPI {
    private static MobThreatService mobThreatService;

    public static void transferMobThreat(UUID from, LivingEntity target) {
        mobThreatService.transferMobThreat(from, target);
    }

    public static void addThreat(Creature mob, UUID attackerUid, long threat) {
        mobThreatService.addThreat(mob, attackerUid, threat);
    }

    public static void setThreat(Creature mob, UUID attackerUid, long threat) {
        mobThreatService.setThreat(mob, attackerUid, threat);
    }

    public static void deleteThreat(Creature mob, UUID attackerUid) {
        mobThreatService.deleteThreat(mob, attackerUid);
    }

    public static void topThreat(Creature mob, UUID attackerUid) {
        mobThreatService.topThreat(mob, attackerUid);
    }

    public static Optional<UUID> getTopThreat(Creature mob) {
        return mobThreatService.getTopThreat(mob);
    }

    public static void setTarget(Creature mob) {
        mobThreatService.setTarget(mob);
    }

    public static void clearThreat(UUID uuid) {
        mobThreatService.clearThreat(uuid);
    }

    public static void removeMobThreat(UUID mobUid) {
        mobThreatService.removeMobThreat(mobUid);
    }

    public static Map<UUID, Long> getMobThreatMap(Creature mob) {
        return mobThreatService.getMobThreatMap(mob);
    }

    @Autowired
    public void setMobThreatService(MobThreatService mobThreatService) {
        MobThreatAPI.mobThreatService = mobThreatService;
    }
}
