package top.nustar.nustarmythicmobsextension.adapter;

import java.util.HashSet;

public abstract class SkillMetadataAdapter<T> extends AbstractAdapter<T> {
    public SkillMetadataAdapter(T object) {
        super(object);
    }

    public abstract SkillCasterAdapter<?> getCaster();
    public abstract HashSet<AbstractEntityAdapter<?>> getEntityTargets();
}
