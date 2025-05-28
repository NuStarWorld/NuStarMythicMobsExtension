package top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.placeholder.helper;

import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import top.nustar.nustarmythicmobsextension.adapter.impl.mm5_6_0.placeholder.PlaceholderDoubleAdapterImpl;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.PlaceholderDoubleAdapter;
import top.nustar.nustarmythicmobsextension.adapter.placeholder.helper.PlaceholderDoubleHelper;

public class PlaceholderDoubleHelperImpl implements PlaceholderDoubleHelper<PlaceholderDouble> {
    @Override
    public PlaceholderDoubleAdapter<PlaceholderDouble> of(String string) {
        return new PlaceholderDoubleAdapterImpl(PlaceholderDouble.of(string));
    }
}
