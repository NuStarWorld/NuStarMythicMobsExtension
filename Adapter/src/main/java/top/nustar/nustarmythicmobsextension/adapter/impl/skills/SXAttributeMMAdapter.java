package top.nustar.nustarmythicmobsextension.adapter.impl.skills;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.LivingEntity;
import team.idealstate.sugar.logging.Log;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.*;
import top.nustar.nustarmythicmobsextension.utils.DamageUtil;

import java.util.Arrays;
import java.util.List;

@NativeObfuscation
public class SXAttributeMMAdapter {
    protected final PlaceholderStringAdapter<?> attrName;
    protected final boolean preventImmunity;
    protected final boolean preventKnockback;

    public SXAttributeMMAdapter(PlaceholderStringHelper<?> placeholderStringHelper, MythicLineConfigAdapter<?> mlc) {
        this.attrName = placeholderStringHelper.of(mlc.getString(new String[]{"attr", "a"}));
        this.preventImmunity = mlc.getBoolean(new String[]{"preventImmunity", "pi"}, false);
        this.preventKnockback = mlc.getBoolean(new String[]{"preventKnockback", "pk"}, false);
    }

    @NativeObfuscation
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        try {
            LivingEntity entity = (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
            List<String> attr = Arrays.asList(attrName.get(skillMetadata, abstractEntity).split(","));
            SXAttributeData statsData = SXAttribute.getApi().getLoreData(entity, null, attr);
            if (statsData != null && statsData.isValid()) {
                SXAttribute.getApi().setEntityAPIData(SXAttributeMMAdapter.class, entity.getUniqueId(), statsData);
                SXAttribute.getApi().updateStats(entity);
            }
            DamageUtil.damage(skillMetadata, abstractEntity, preventImmunity, preventKnockback);
            SXAttribute.getApi().removeEntityAPIData(SXAttributeMMAdapter.class, entity.getUniqueId());
            SXAttribute.getApi().updateStats(entity);
            return true;
        } catch (Exception e) {
            Log.error(e);
            return false;
        }
    }
}
