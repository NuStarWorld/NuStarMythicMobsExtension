package top.nustar.nustarmythicmobsextension.adapter.impl.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.next.calculate.Expression;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;

/**
 * @author : NuStar
 * Date : 2025/6/25 21:32
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class NuStarThreatAdapter implements NuStarSkill, GlobalVariable{
    protected final String mode;
    protected final Expression amount;
    protected final PlaceholderDoubleAdapter<?> multiple;
    protected final MobThreatManager mobThreatManager;
    protected final MainConfiguration mainConfiguration;

    public NuStarThreatAdapter(PlaceholderDoubleHelper<?> placeholderDoubleHelper,
                               MythicLineConfigAdapter<?> mlc,
                               MainConfiguration mainConfiguration,
                               MobThreatManager mobThreatManager) {
        this.mode = mlc.getString(new String[]{"mode", "m"}, "add");
        this.amount = new Expression(mlc.getString(new String[]{"amount", "a"}, "0")).compile();
        this.multiple = placeholderDoubleHelper.of(mlc.getString(new String[]{"multiple", "m"}, "1"));
        this.mobThreatManager = mobThreatManager;
        this.mainConfiguration = mainConfiguration;
    }

    @Override
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        LivingEntity caster = (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        LivingEntity entity = (LivingEntity) abstractEntity.getBukkitEntity();
        long amount = this.amount.calculate(parseExpressionContext(skillMetadata, abstractEntity, mainConfiguration.getVariables())).longValue();
        if (!mode.equals("transfer") && !(entity instanceof Creature)) return false;
        switch (mode) {
            case "add":
                mobThreatManager.updateMobThreat((Creature) entity, mobThreat -> mobThreat.addEntityThreat(caster.getUniqueId(), amount));
                break;
            case "set":
                mobThreatManager.updateMobThreat((Creature) entity, mobThreat -> mobThreat.setEntityThreat(caster.getUniqueId(), amount));
                break;
            case "delete":
                mobThreatManager.updateMobThreat((Creature) entity, mobThreat -> mobThreat.removeEntityThreat(caster.getUniqueId()));
                break;
            case "top":
                mobThreatManager.updateMobThreat((Creature) entity, mobThreat -> mobThreat.setTopThreat(caster.getUniqueId()));
                break;
            case "transfer":
                mobThreatManager.transferMobThreat(caster.getUniqueId(), entity);
                break;
        }
        return true;
    }
}
