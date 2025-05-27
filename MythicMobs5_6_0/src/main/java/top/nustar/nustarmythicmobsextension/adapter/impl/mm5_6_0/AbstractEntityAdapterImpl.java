package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.adapters.AbstractEntity;
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

    @Override
    public void setMetadata(String key, Object value) {
        getActualObject().setMetadata(key, value);
    }

    @Override
    public void removeMetadata(String key) {
        getActualObject().removeMetadata(key);
    }
}
