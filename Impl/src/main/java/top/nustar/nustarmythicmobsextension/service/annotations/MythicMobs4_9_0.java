package top.nustar.nustarmythicmobsextension.service.annotations;

import team.idealstate.sugar.next.context.annotation.feature.DependsOn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : NuStar
 * Date : 2025/6/21 21:11
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Target(ElementType.TYPE)
@DependsOn(classes = "io.lumine.xikage.mythicmobs.utils.config.file.YamlConfiguration")
@Retention(RetentionPolicy.RUNTIME)
public @interface MythicMobs4_9_0 {
}
