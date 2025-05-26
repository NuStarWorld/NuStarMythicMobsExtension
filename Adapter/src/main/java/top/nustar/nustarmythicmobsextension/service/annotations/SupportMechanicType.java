package top.nustar.nustarmythicmobsextension.service.annotations;

import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportMechanicType {
    MechanicType type();
    MythicMobsVersion version();
}
