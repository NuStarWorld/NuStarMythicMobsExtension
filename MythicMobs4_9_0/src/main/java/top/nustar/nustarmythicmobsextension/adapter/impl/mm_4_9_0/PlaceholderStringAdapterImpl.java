package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
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
