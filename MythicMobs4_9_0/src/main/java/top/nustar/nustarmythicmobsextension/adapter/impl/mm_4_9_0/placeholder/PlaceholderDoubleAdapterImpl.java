package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import lombok.NonNull;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;

public class PlaceholderDoubleAdapterImpl extends PlaceholderDoubleAdapter<PlaceholderDouble> {
    public PlaceholderDoubleAdapterImpl(@NonNull PlaceholderDouble actualObject) {
        super(actualObject);
    }

    @Override
    public double get(SkillMetadataAdapter<?> skillMetadataAdapter, AbstractEntityAdapter<?> abstractEntityAdapter) {
        return getActualObject().get((SkillMetadata)skillMetadataAdapter.getActualObject(), (AbstractEntity) abstractEntityAdapter.getActualObject());
    }
}
