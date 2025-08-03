package top.nustar.nustarmythicmobsextension.service;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.context.annotation.component.Service;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.adapter.MythicInstance;
import top.nustar.nustarmythicmobsextension.api.service.MobThreatService;
import top.nustar.nustarmythicmobsextension.entity.MobThreat;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author : NuStar
 * Date : 2025/8/3 12:17
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Service
@Scope(Scope.SINGLETON)
@SuppressWarnings("unused")
public class MobThreatServiceImpl implements MobThreatService {

    private volatile MobThreatManager mobThreatManager;
    private volatile MythicInstance mythicInstance;

    @Override
    public void transferMobThreat(UUID from, LivingEntity target) {
        if (mythicInstance.getMobManager().getMythicMobInstance(target).getActualObject() != null) return;
        mobThreatManager.transferMobThreat(from, target);
    }

    @Override
    public void addThreat(Creature mob, UUID uuid, long threat) {
        mobThreatManager.updateMobThreat(mob, mobThreat -> mobThreat.addEntityThreat(uuid, threat));
    }

    @Override
    public void setThreat(Creature mob, UUID uuid, long threat) {
        mobThreatManager.updateMobThreat(mob, mobThreat -> mobThreat.setEntityThreat(uuid, threat));
    }

    @Override
    public void deleteThreat(Creature mob, UUID uuid) {
        mobThreatManager.updateMobThreat(mob, mobThreat -> mobThreat.removeEntityThreat(uuid));
    }

    @Override
    public void topThreat(Creature mob, UUID uuid) {
        mobThreatManager.updateMobThreat(mob, mobThreat -> mobThreat.setTopThreat(uuid));
    }

    @Override
    public Optional<UUID> getTopThreat(Creature mob) {
        return mobThreatManager.getMobThreat(mob).getTopThreat();
    }

    @Override
    public void setTarget(Creature mob) {
        mobThreatManager.updateMobThreat(mob, MobThreat::setTarget);
    }

    @Override
    public void clearThreat(UUID uuid) {
        mobThreatManager.clearThreat(uuid);
    }

    @Override
    public void removeMobThreat(UUID mobUid) {
        mobThreatManager.removeMobThreat(mobUid);
    }

    @Override
    public Map<UUID, Long> getMobThreatMap(Creature mob) {
        return new HashMap<>(mobThreatManager.getMobThreat(mob).getEntityThreatMap());
    }

    @Autowired
    public void setMobThreatManager(MobThreatManager mobThreatManager) {
        this.mobThreatManager = mobThreatManager;
    }

    @Autowired
    public void setMythicInstance(MythicInstance mythicInstance) {
        this.mythicInstance = mythicInstance;
    }
}
