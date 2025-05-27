package top.nustar.nustarmythicmobsextension.adapter;

import lombok.NonNull;
import org.bukkit.entity.Entity;

public abstract class AbstractEntityAdapter<T> extends AbstractAdapter<T> {
    public AbstractEntityAdapter(@NonNull T actualObject) {
        super(actualObject);
    }

    public abstract Entity getBukkitEntity();
    public abstract void setMetadata(String key, Object value);
    public abstract void removeMetadata(String key);
}
