package top.nustar.nustarmythicmobsextension.adapter.impl.targets;

import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;

import java.util.HashSet;

@NativeObfuscation
public class EntitiesInTargetsSelectorAdapter {

    @NativeObfuscation
    public HashSet<AbstractEntityAdapter<?>> getEntities(SkillMetadataAdapter<?> skillMetadataAdapter) {
        return new HashSet<>(skillMetadataAdapter.getEntityTargets());
    }
}
