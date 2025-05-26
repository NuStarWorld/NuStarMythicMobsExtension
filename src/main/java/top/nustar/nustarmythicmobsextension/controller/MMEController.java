package top.nustar.nustarmythicmobsextension.controller;

import team.idealstate.sugar.logging.Log;
import team.idealstate.sugar.next.command.Command;
import team.idealstate.sugar.next.command.CommandResult;
import team.idealstate.sugar.next.command.annotation.CommandHandler;
import team.idealstate.sugar.next.context.Bean;
import team.idealstate.sugar.next.context.Context;
import team.idealstate.sugar.next.context.annotation.component.Controller;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Named;
import team.idealstate.sugar.next.context.aware.ContextAware;
import team.idealstate.sugar.validate.Validation;
import team.idealstate.sugar.validate.annotation.NotNull;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;
import top.nustar.nustarmythicmobsextension.service.ConfigService;

@Named("nsme")
@Controller
@SuppressWarnings({"unused"})
public class MMEController implements Command, ContextAware {
    private volatile Context context;
    private volatile ConfigService configService;
    @CommandHandler
    @NotNull
    public CommandResult reload() {
        try {
            Bean<MainConfiguration> bean = context.getBean(MainConfiguration.class);
            Validation.notNull(bean, "未能获取到配置 Bean。");
            assert bean != null;
            configService.setMainConfiguration(bean.getInstance());
        } catch (Throwable e) {
            Log.error(e);
            return CommandResult.failure("未能完成配置重载，错误信息请查看日志输出。");
        }
        return CommandResult.success("已完成配置重载");
    }

    @Override
    public void setContext(@NotNull Context context) {
        this.context = context;
    }
    @Autowired
    public void setConfigService(@NotNull ConfigService configService) {
        this.configService = configService;
    }
}
