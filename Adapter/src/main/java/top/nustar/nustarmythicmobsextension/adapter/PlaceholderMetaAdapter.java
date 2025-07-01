package top.nustar.nustarmythicmobsextension.adapter;

/**
 * @author : NuStar
 * Date : 2025/7/1 00:28
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public abstract class PlaceholderMetaAdapter<T> extends AbstractAdapter<T> {
    public PlaceholderMetaAdapter(T actualObject) {
        super(actualObject);
    }

    public SkillCasterAdapter<?> getCaster() {
        return null;
    }

    public AbstractEntityAdapter<?> getTrigger() {
        return null;
    }
}
