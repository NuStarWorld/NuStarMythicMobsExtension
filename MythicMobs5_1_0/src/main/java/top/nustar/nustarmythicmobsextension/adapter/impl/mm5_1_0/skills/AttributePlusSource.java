package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.PlaceholderStringHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.AttributePlusSourceAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;

public class AttributePlusSource extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final AttributePlusSourceAdapter attributePlusSourceAdapter;
    public AttributePlusSource(String skill, SkillExecutor executor, MythicLineConfig mlc) {
        super(executor, skill, mlc);
        this.attributePlusSourceAdapter = new AttributePlusSourceAdapter(
                new PlaceholderStringHelperImpl(),
                new MythicLineConfigAdapterImpl(mlc)
        );
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        boolean result = attributePlusSourceAdapter.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
        return result ? SkillResult.SUCCESS : SkillResult.ERROR;
    }
}
