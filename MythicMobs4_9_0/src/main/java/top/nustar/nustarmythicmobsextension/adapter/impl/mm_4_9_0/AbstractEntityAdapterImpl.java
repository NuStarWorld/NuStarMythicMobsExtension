package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import org.bukkit.entity.Entity;
import top.nustar.nustarmythicmobsextension.adapter.AbstractEntityAdapter;

public class AbstractEntityAdapterImpl extends AbstractEntityAdapter<AbstractEntity> {
    public AbstractEntityAdapterImpl(AbstractEntity object) {
        super(object);
    }

    @Override
    public Entity getBukkitEntity() {
        return getActualObject().getBukkitEntity();
    }
}
