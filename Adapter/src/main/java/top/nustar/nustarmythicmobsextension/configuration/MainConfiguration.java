package top.nustar.nustarmythicmobsextension.configuration;

import lombok.Data;
import team.idealstate.sugar.internal.com.fasterxml.jackson.annotation.JsonProperty;
import team.idealstate.sugar.internal.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import team.idealstate.sugar.next.context.annotation.component.Configuration;
import team.idealstate.sugar.next.context.annotation.feature.Scope;

import java.util.List;

@Configuration(uri = "/config.yml", release = "bundled:/config.yml")
@Scope(Scope.PROTOTYPE)
@Data
public class MainConfiguration {

    private final boolean threat;

    @JsonDeserialize(contentAs = String.class)
    @JsonProperty("white-attr-list")
    private final List<String> whiteAttrList;
}
