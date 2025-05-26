package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.PlaceholderStringHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.SXAttributeMMAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;

public class SXAttributeMM extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final SXAttributeMMAdapter sxAttributeMMAdapter;

    public SXAttributeMM(String skill, SkillExecutor manager, MythicLineConfig mlc) {
        super(manager, skill, mlc);
        this.sxAttributeMMAdapter = new SXAttributeMMAdapter(
                new PlaceholderStringHelperImpl(),
                new MythicLineConfigAdapterImpl(mlc)
        );
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        boolean result = sxAttributeMMAdapter.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
        return result ? SkillResult.SUCCESS : SkillResult.ERROR;
    }

}
