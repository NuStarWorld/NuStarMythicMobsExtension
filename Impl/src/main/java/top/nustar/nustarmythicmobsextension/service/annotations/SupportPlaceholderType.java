package top.nustar.nustarmythicmobsextension.service.annotations;

import top.nustar.nustarmythicmobsextension.service.enums.PlaceholderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : NuStar
 * Date : 2025/6/30 22:34
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportPlaceholderType {
    PlaceholderType type();
}
