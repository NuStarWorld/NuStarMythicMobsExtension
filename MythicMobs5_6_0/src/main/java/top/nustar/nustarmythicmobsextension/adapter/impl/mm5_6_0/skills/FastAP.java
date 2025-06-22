package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.skills;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.AbstractEntityAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.MythicLineConfigAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.SkillMetadataAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.FastAPAdapter;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;

/**
 * @author : NuStar
 * Date : 2025/6/22 21:48
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public class FastAP extends SkillMechanic implements ITargetedEntitySkill, NuStarMechanic {
    private final FastAPAdapter fastAPAdapter;

    public FastAP(String line, SkillExecutor manager, MythicLineConfig mlc, MainConfiguration mainConfiguration) {
        super(manager, line, mlc);
        this.fastAPAdapter = new FastAPAdapter(new MythicLineConfigAdapterImpl(mlc), mainConfiguration);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        boolean result =  fastAPAdapter.castAtEntity(
                new SkillMetadataAdapterImpl(skillMetadata), new AbstractEntityAdapterImpl(abstractEntity));
        return result ? SkillResult.SUCCESS : SkillResult.ERROR;
    }
}
