package top.nustar.nustarmythicmobsextension.utils;

import team.idealstate.sugar.next.context.ContextHolder;
import team.idealstate.sugar.next.context.annotation.component.Component;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import team.idealstate.sugar.next.context.aware.ContextHolderAware;
import team.idealstate.sugar.validate.annotation.NotNull;

@Component
@Scope(Scope.SINGLETON)
public class InstanceUtil implements ContextHolderAware {
    private static volatile ContextHolder contextHolder;
    @Override
    public void setContextHolder(@NotNull ContextHolder contextHolder) {
        InstanceUtil.contextHolder = contextHolder;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return clazz.cast(contextHolder);
    }
}
