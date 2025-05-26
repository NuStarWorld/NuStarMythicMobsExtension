package top.nustar.nustarmythicmobsextension;

import team.idealstate.minecraft.next.spigot.api.SpigotPlugin;
import team.idealstate.sugar.banner.Banner;
import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.next.context.Context;
import team.idealstate.sugar.next.context.annotation.feature.EnableSugar;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.nustarmythicmobsextension.utils.UpdateUtil;

@EnableSugar
public class NuStarMythicMobsExtension extends SpigotPlugin {
    @Override
    public void onInitialize(@NotNull Context context) {
    }

    @Override
    public void onInitialized(@NotNull Context context) {
    }

    @Override
    public void onLoad(@NotNull Context context) {
    }

    @Override
    public void onLoaded(@NotNull Context context) {
    }

    @Override
    public void onEnable(@NotNull Context context) {
    }

    @Override
    public void onEnabled(@NotNull Context context) {
        Banner.lines(getClass()).forEach(Log::info);
        UpdateUtil.checkUpdate();
    }

    @Override
    public void onDisable(@NotNull Context context) {
    }

    @Override
    public void onDisabled(@NotNull Context context) {
    }

    @Override
    public void onDestroy(@NotNull Context context) {
    }

    @Override
    public void onDestroyed(@NotNull Context context) {
    }
}
