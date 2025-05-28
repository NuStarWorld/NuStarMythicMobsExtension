package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.placeholder;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
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
