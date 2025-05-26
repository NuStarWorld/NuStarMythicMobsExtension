package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.targets;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.targets.EntitiesInTargetsSelectorAdapter;

import java.util.HashSet;

public class EntitiesInTargetsSelector extends IEntitySelector implements NuStarTargerSelector {
    private  final EntitiesInTargetsSelectorAdapter entitiesInTargetsSelectorAdapter;
    public EntitiesInTargetsSelector(SkillExecutor executor, MythicLineConfig mlc) {
        super(executor, mlc);
        this.entitiesInTargetsSelectorAdapter = new EntitiesInTargetsSelectorAdapter();
    }

    @Override
    public HashSet<AbstractEntity> getEntities(SkillMetadata skillMetadata) {
        HashSet<AbstractEntityAdapter<?>> entities = entitiesInTargetsSelectorAdapter.getEntities(new SkillMetadataAdapterImpl(skillMetadata));
        HashSet<AbstractEntity> abstractEntities = new HashSet<>();
        for (AbstractEntityAdapter<?> entity : entities) {
            abstractEntities.add((AbstractEntity) entity.getActualObject());
        }
        return abstractEntities;
    }
}
