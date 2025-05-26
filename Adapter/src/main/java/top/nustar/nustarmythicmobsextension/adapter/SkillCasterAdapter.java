package top.nustar.nustarmythicmobsextension.adapter;

public abstract class SkillCasterAdapter<T> extends AbstractAdapter<T> {

    public SkillCasterAdapter(T object) {
        super(object);
    }

    public abstract AbstractEntityAdapter<?> getEntity();
    public abstract void setUsingDamageSkill(boolean b);
}
