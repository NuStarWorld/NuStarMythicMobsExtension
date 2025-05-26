package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillCasterAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

import java.util.HashSet;

public class SkillMetadataAdapterImpl extends SkillMetadataAdapter<SkillMetadata> {

    public SkillMetadataAdapterImpl(SkillMetadata metadata) {
        super(metadata);
    }

    @Override
    public SkillCasterAdapter<?> getCaster() {
        return new SkillCasterAdapterImpl(getActualObject().getCaster());
    }

    @Override
    public HashSet<AbstractEntityAdapter<?>> getEntityTargets() {
        HashSet<AbstractEntityAdapter<?>> adapters = new HashSet<>();
        for (AbstractEntity abstractEntity : getActualObject().getEntityTargets()) {
            adapters.add(new AbstractEntityAdapterImpl(abstractEntity));
        }
        return adapters;
    }
}
