package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.placeholder.helper.PlaceholderDoubleHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarThreatAdapter;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.manager.MobThreatManager;

/**
 * @author : NuStar
 * Date : 2025/6/25 23:12
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class NuStarThreat extends SkillMechanic implements NuStarMechanic, ITargetedEntitySkill {
    private final NuStarSkill nuStarThreatSkill;

    public NuStarThreat(String skill, SkillExecutor executor, MythicLineConfig mlc, MainConfiguration mainConfiguration, MobThreatManager mobThreatManager) {
        super(executor, skill, mlc);
        this.nuStarThreatSkill = new NuStarThreatAdapter(new PlaceholderDoubleHelperImpl(), new MythicLineConfigAdapterImpl(mlc), mainConfiguration, mobThreatManager);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        boolean result =  nuStarThreatSkill.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
        return result ? SkillResult.SUCCESS : SkillResult.ERROR;
    }
}
