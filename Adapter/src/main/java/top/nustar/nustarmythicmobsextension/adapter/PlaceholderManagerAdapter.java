package top.nustar.nustarmythicmobsextension.adapter;

/**
 * @author : NuStar
 * Date : 2025/7/1 19:41
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public abstract class PlaceholderManagerAdapter<T> extends AbstractAdapter<T> {
    public PlaceholderManagerAdapter(T actualObject) {
        super(actualObject);
    }

    public abstract void register(String key, PlaceholderAdapter<?> transformer);
}
