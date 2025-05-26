package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0;

import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderStringAdapter;
import top.nustar.nustarmythicmobsextension.adapter.PlaceholderStringHelper;

public class PlaceholderStringHelperImpl implements PlaceholderStringHelper<PlaceholderString> {
    @Override
    public PlaceholderStringAdapter<PlaceholderString> of(String string) {
        return new PlaceholderStringAdapterImpl(PlaceholderString.of(string));
    }
}
