package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.PlaceholderStringHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.AttributePlusMMAdapter;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;

public class AttributePlusMM extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final AttributePlusMMAdapter attributePlusMMAdapter;

    public AttributePlusMM(String skill, MythicLineConfig mlc, MainConfiguration mainConfiguration) {
        super(skill, mlc);
        this.attributePlusMMAdapter = new AttributePlusMMAdapter(
                new PlaceholderStringHelperImpl(),
                new MythicLineConfigAdapterImpl(mlc),
                mainConfiguration);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return attributePlusMMAdapter.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
    }
}
