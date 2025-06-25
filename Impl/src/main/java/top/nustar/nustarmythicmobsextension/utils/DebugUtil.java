package top.nustar.nustarmythicmobsextension.utils;

import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.service.ConfigService;

/**
 * @author : NuStar
 * Date : 2025/6/25 23:27
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
@Component
@Scope(Scope.SINGLETON)
@SuppressWarnings({"unused"})
public class DebugUtil {
    private static volatile ConfigService configService;

    @Autowired
    public void setConfigService(ConfigService configService) {
        DebugUtil.configService = configService;
    }

    public static void debug(String msg) {
        if (configService.getMainConfiguration().isDebug()) {
            Log.info(String.format("[debug] %s", msg));
        }
    }
}
