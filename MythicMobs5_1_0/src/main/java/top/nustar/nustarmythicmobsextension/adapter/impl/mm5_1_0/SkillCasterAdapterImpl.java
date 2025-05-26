package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0;

import io.lumine.mythic.api.skills.SkillCaster;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillCasterAdapter;

public class SkillCasterAdapterImpl extends SkillCasterAdapter<SkillCaster> {

    public SkillCasterAdapterImpl(SkillCaster object) {
        super(object);
    }

    @Override
    public AbstractEntityAdapter<?> getEntity() {
        return new AbstractEntityAdapterImpl(getActualObject().getEntity());
    }

    @Override
    public void setUsingDamageSkill(boolean b) {
        getActualObject().setUsingDamageSkill(b);
    }
}
