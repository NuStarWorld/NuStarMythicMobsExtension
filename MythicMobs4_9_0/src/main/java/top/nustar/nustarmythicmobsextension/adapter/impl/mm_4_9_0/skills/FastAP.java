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
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.FastAPAdapter;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;

/**
 * @author : NuStar
 * Date : 2025/6/22 21:48
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class FastAP extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final FastAPAdapter fastAPAdapter;

    public FastAP(String skill, MythicLineConfig mlc, MainConfiguration mainConfiguration) {
        super(skill, mlc);
        this.fastAPAdapter = new FastAPAdapter(new MythicLineConfigAdapterImpl(mlc), mainConfiguration);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return fastAPAdapter.castAtEntity(
                new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
    }
}
