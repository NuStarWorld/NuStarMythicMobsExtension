package top.nustar.nustarmythicmobsextension.adapter.impl.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.serverct.ersha.AttributePlus;
import org.serverct.ersha.api.AttributeAPI;
import org.serverct.ersha.attribute.data.AttributeData;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import top.nustar.nustarmythicmobsextension.adapter.*;
import top.nustar.nustarmythicmobsextension.utils.InstanceUtil;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@NativeObfuscation
public class AttributePlusSourceAdapter {
    protected final PlaceholderStringAdapter<?> attrName;
    protected final boolean persistent;
    protected double time;

    public AttributePlusSourceAdapter(PlaceholderStringHelper<?> placeholderStringHelper, MythicLineConfigAdapter<?> mlc) {
        this.attrName = placeholderStringHelper.of(mlc.getString(new String[] {"attr","a"}));
        this.persistent = mlc.getBoolean(new String[] {"persistent","p"}, false);
        if (persistent) {
            this.time = mlc.getDouble(new String[] {"time","t"});
        }
    }

    @NativeObfuscation
    public boolean castAtEntity(SkillMetadataAdapter<?> skillMetadata, AbstractEntityAdapter<?> abstractEntity) {
        LivingEntity entity = (LivingEntity) skillMetadata.getCaster().getEntity().getBukkitEntity();
        List<String> attr = Arrays.asList(attrName.get(skillMetadata,abstractEntity).split(","));
        AttributeData data = AttributePlus.INSTANCE.getAttributeManager().getAttributeData(entity);
        String source = "APSource" + UUID.randomUUID();
        AttributeAPI.addSourceAttribute(data,source,attr);
        AttributeAPI.updateAttribute(entity);
        if (persistent) {
            new BukkitRunnable() {
                @Override
                public void run(){
                    AttributeAPI.takeSourceAttribute(data,source);
                }
            }.runTaskLaterAsynchronously(InstanceUtil.getInstance(Plugin.class), (long) (time * 20));
        }
        return true;
    }
}
