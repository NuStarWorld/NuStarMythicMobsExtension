package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.helper.PlaceholderDoubleHelperImpl;
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

    public NuStarThreat(String skill, MythicLineConfig mlc, MainConfiguration mainConfiguration, MobThreatManager mobThreatManager) {
        super(skill, mlc);
        this.nuStarThreatSkill = new NuStarThreatAdapter(new PlaceholderDoubleHelperImpl(), new MythicLineConfigAdapterImpl(mlc), mainConfiguration, mobThreatManager);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return nuStarThreatSkill.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
    }
}
