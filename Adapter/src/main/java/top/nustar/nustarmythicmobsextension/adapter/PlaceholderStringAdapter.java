package top.nustar.nustarmythicmobsextension.adapter;

import lombok.NonNull;

public abstract class PlaceholderStringAdapter<T> extends AbstractAdapter<T>{
    public PlaceholderStringAdapter(@NonNull T actualObject) {
        super(actualObject);
    }
    public abstract String get(SkillMetadataAdapter<?> data, AbstractEntityAdapter<?> target);
}
