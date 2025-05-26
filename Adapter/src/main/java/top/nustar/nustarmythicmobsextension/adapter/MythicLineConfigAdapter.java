package top.nustar.nustarmythicmobsextension.adapter;

import lombok.NonNull;

public abstract class MythicLineConfigAdapter<T> extends AbstractAdapter<T> {
    public MythicLineConfigAdapter(@NonNull T actualObject) {
        super(actualObject);
    }
    public abstract String getString(String[] key);
    public abstract boolean getBoolean(String[] key, boolean def);
    public abstract double getDouble(String[] key);
}
