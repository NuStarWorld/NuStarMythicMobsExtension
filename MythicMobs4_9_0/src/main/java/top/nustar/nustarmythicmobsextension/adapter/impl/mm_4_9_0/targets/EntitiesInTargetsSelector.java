package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.targets;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.targets.EntitiesInTargetsSelectorAdapter;

import java.util.HashSet;

public class EntitiesInTargetsSelector extends IEntitySelector implements NuStarTargerSelector {
    private  final EntitiesInTargetsSelectorAdapter entitiesInTargetsSelectorAdapter;
    public EntitiesInTargetsSelector(MythicLineConfig mlc) {
        super(mlc);
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
