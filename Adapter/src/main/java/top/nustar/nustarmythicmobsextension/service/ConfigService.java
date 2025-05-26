package top.nustar.nustarmythicmobsextension.service;

import lombok.Getter;
import team.idealstate.sugar.next.context.annotation.component.Service;
import team.idealstate.sugar.next.context.annotation.feature.Autowired;
import team.idealstate.sugar.next.context.annotation.feature.Scope;
import top.nustar.nustarmythicmobsextension.configuration.MainConfiguration;

@Getter
@Service
@Scope(Scope.SINGLETON)
@SuppressWarnings({"unused"})
public class ConfigService {
    private volatile MainConfiguration mainConfiguration;

    @Autowired
    public void setMainConfiguration(MainConfiguration mainConfiguration) {
        this.mainConfiguration = mainConfiguration;
    }
}
