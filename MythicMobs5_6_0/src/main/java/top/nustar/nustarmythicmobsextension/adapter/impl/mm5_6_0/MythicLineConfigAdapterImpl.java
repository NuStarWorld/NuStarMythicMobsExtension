package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0;

import io.lumine.mythic.api.config.MythicLineConfig;
import lombok.NonNull;
import top.nustar.nustarmythicmobsextension.adapter.MythicLineConfigAdapter;

public class MythicLineConfigAdapterImpl extends MythicLineConfigAdapter<MythicLineConfig> {
    public MythicLineConfigAdapterImpl(@NonNull MythicLineConfig actualObject) {
        super(actualObject);
    }

    @Override
    public String getString(String[] key) {
        return getActualObject().getString(key);
    }

    @Override
    public boolean getBoolean(String[] key, boolean def) {
        return getActualObject().getBoolean(key, def);
    }
    @Override
    public double getDouble(String[] key) {
        return getActualObject().getDouble(key, 1.0);
    }
}
