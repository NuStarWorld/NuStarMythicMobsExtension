package top.nustar.nustarmythicmobsextension.adapter.impl.skills.mechanics;

import org.bukkit.entity.LivingEntity;
import org.serverct.ersha.AttributePlus;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import team.idealstate.sugar.next.calculate.Expression;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;
import top.nustar.nustarmythicmobsextension.adapter.SkillMetadataAdapter;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.GlobalVariable;
import top.nustar.nustarmythicmobsextension.adapter.impl.skills.NuStarSkill;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.exception.NSMMEException;
import top.nustar.nustarmythicmobsextension.utils.AttributeUtils;
import top.nustar.nustarmythicmobsextension.utils.DamageUtil;

import java.util.*;

/**
 * @author : NuStar
 * Date : 2025/6/22 15:48
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@NativeObfuscation
public class FastAPAdapter implements NuStarSkill, GlobalVariable {
    private final MainConfiguration mainConfiguration;
    private final Map<String, Expression> attrExpressionMap = new HashMap<>();
    protected final boolean clear;
    protected final boolean preventImmunity;
    protected final boolean preventKnockback;

    public FastAPAdapter(MythicLineConfigAdapter<?> mlc, MainConfiguration mainConfiguration) {
        this.mainConfiguration = mainConfiguration;
        String attrString = mlc.getString(new String[]{"attr", "a"});
        String[] attrSplit = attrString.split(",");
        for (String attr : attrSplit) {
            String[] attrLine = attr.split(":");
            if (attrLine.length != 2) {
                throw new NSMMEException("Invalid attribute format:" + attr);
            }
            attrExpressionMap.put(attrLine[0], new Expression(attrLine[1]).compile());
        }
        this.clear = mlc.getBoolean(new String[] {"clear", "c"}, false);
        this.preventImmunity = mlc.getBoolean(new String[] {"preventImmunity", "pi"}, false);
        this.preventKnockback = mlc.getBoolean(new String[] {"preventKnockback", "pk"}, false);
    }

    @Override
    @NativeObfuscation
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        LivingEntity caster = (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        List<String> attrList = new ArrayList<>(attrExpressionMap.size());
        for (Map.Entry<String, Expression> entry : attrExpressionMap.entrySet()) {
            attrList.add(entry.getKey() + ":" + entry.getValue().calculate(parseExpressionContext(skillMetadata, abstractEntity, mainConfiguration.getVariables())));
        }
        AttributeData data = AttributePlus.INSTANCE.getAttributeManager().getAttributeData(caster);
        AttributeAPI.addSourceAttribute(data, "APMM_XULI", Collections.singletonList("蓄力加成:100"));
        if (clear) {
            AttributeData newData = AttributeData.Companion.create(caster);
            AttributeAPI.addSourceAttribute(newData, "APMM_XULI", Collections.singletonList("蓄力加成:100"));
            AttributeAPI.addSourceAttribute(
                    newData,
                    "APMM_WhiteList",
                    AttributeUtils.getWhiteAttributeList(data, mainConfiguration.getWhiteAttrList()));
            AttributeAPI.addSourceAttribute(newData, "APMM", attrList);
            AttributePlus.INSTANCE.getAttributeManager().entityAttributeData.put(caster.getUniqueId(), newData);
            DamageUtil.damage(skillMetadata, abstractEntity, preventImmunity, preventKnockback);
            AttributePlus.INSTANCE.getAttributeManager().entityAttributeData.put(caster.getUniqueId(), data);
        } else {
            AttributeAPI.addSourceAttribute(data, "APMM", attrList);
            DamageUtil.damage(skillMetadata, abstractEntity, preventImmunity, preventKnockback);
            AttributeAPI.takeSourceAttribute(data, "APMM");
        }
        AttributeAPI.takeSourceAttribute(data, "APMM_XULI");
        return true;
    }
}
