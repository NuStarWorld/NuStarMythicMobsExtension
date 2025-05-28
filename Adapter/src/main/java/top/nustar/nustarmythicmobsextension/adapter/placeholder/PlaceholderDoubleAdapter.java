package top.nustar.nustarmythicmobsextension.adapter.placeholder;

import lombok.NonNull;
import top.nustar.nustarmythicmobsextension.adapter.AbstractAdapter;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

public abstract class PlaceholderDoubleAdapter<T> extends AbstractAdapter<T> {
    public PlaceholderDoubleAdapter(@NonNull T actualObject) {
        super(actualObject);
    }
    public abstract double get(SkillMetadataAdapter<?> skillMetadataAdapter, AbstractEntityAdapter<?> abstractEntityAdapter);
}
