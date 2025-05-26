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
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.SXAttributeMMAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;

public class SXAttributeMM extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final SXAttributeMMAdapter sxAttributeMMAdapter;
    public SXAttributeMM(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        this.sxAttributeMMAdapter = new SXAttributeMMAdapter(
                new PlaceholderStringHelperImpl(),
                new MythicLineConfigAdapterImpl(mlc)
        );
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return sxAttributeMMAdapter.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
    }
}
