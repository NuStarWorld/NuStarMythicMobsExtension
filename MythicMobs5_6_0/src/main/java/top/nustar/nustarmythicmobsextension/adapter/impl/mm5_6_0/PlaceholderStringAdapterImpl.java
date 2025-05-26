package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import lombok.NonNull;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

public class PlaceholderStringAdapterImpl extends PlaceholderStringAdapter<PlaceholderString> {
    public PlaceholderStringAdapterImpl(@NonNull PlaceholderString actualObject) {
        super(actualObject);
    }

    @Override
    public String get(SkillMetadataAdapter<?> data, AbstractEntityAdapter<?> target) {
        return getActualObject().get((SkillMetadata)data.getActualObject(), (AbstractEntity) target.getActualObject());
    }

}
