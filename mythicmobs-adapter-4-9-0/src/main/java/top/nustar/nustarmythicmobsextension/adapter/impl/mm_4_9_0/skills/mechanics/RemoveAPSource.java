package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.skills.mechanics;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.helper.PlaceholderStringHelperImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics.RemoveAPSourceAdapter;

public class RemoveAPSource extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {

    private final NuStarSkill removeAPSourceSkill;

    public RemoveAPSource(String skill, MythicLineConfig mlc) {
        super(skill, mlc);
        removeAPSourceSkill = new RemoveAPSourceAdapter(new PlaceholderStringHelperImpl(), new MythicLineConfigAdapterImpl(mlc));
        this.forceSync = true;
        setAsyncSafe(false);
    }

    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        return removeAPSourceSkill.castAtEntity(new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
    }
}
