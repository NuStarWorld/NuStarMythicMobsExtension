package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_1_0;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderStringHelper;

public class PlaceholderStringHelperImpl implements PlaceholderStringHelper<PlaceholderString> {
    @Override
    public PlaceholderStringAdapter<PlaceholderString> of(String string) {
        return new PlaceholderStringAdapterImpl(PlaceholderString.of(string));
    }
}
