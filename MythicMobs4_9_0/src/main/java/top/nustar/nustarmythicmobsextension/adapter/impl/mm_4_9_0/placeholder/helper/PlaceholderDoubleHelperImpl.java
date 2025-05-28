package top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.helper;

import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm_4_9_0.placeholder.PlaceholderDoubleAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;

public class PlaceholderDoubleHelperImpl implements PlaceholderDoubleHelper<PlaceholderDouble> {
    @Override
    public PlaceholderDoubleAdapter<PlaceholderDouble> of(String string) {
        return new PlaceholderDoubleAdapterImpl(PlaceholderDouble.of(string));
    }
}
