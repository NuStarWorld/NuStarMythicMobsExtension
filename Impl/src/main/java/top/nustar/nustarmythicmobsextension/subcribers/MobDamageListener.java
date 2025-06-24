package top.nustar.nustarmythicmobsextension.subcribers;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import top.nustar.nustarmythicmobsextension.adapter.ActiveMobAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;

import java.util.UUID;

/**
 * @author : NuStar
 * Date : 2025/6/24 19:35
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
public class MobDamageListener implements Listener {
    private volatile MythicInstance mythicInstance;
    private volatile MobThreatManager mobThreatManager;

    @Autowired
    public void setMythicInstance(MythicInstance mythicInstance) {
        this.mythicInstance = mythicInstance;
    }

    @Autowired
    public void setMobThreatManager(MobThreatManager mobThreatManager) {
        this.mobThreatManager = mobThreatManager;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Creature)) return;
        mobThreatManager.removeMobThreat((Creature) entity);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target == null) return;
        Entity entity = event.getEntity();
        if (!validateMM(entity)) return;
        UUID topThreat = mobThreatManager.getMobThreat((Creature) entity).getTopThreat();
        if (topThreat != null) {
            if (topThreat.equals(target.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player) return;
        if (!validateMM(entity)) return;
        long damage = (long) event.getFinalDamage();
        mobThreatManager.updateMobThreat((Creature) entity, mobThreat -> {
            mobThreat.addEntityThreat(damager.getUniqueId(), damage);
            mobThreat.setTarget();
        });
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateMM(Entity entity) {
        if (!(entity instanceof Creature)) return false;
        ActiveMobAdapter<?> activeMobAdapter = mythicInstance.getMobManager().getMythicMobInstance(entity);
        return activeMobAdapter.getActualObject() != null;
    }
}
