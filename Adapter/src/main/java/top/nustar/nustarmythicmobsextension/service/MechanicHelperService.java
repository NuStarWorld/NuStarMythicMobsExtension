package top.nustar.nustarmythicmobsextension.service;

import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarMechanic;
import top.nustar.nustarmythicmobsextension.service.enums.MechanicType;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportMechanicType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;

public interface MechanicHelperService {
    NuStarMechanic findMechanic(Object... objects);
    default MechanicType findMechanicTypeFromService(MechanicHelperService mechanicHelperService) {
        SupportMechanicType supportMechanicType = mechanicHelperService.getClass().getAnnotation(SupportMechanicType.class);
        return supportMechanicType.type();
    }

    default MythicMobsVersion findMythicMobsVersionFromService(MechanicHelperService mechanicHelperService) {
        SupportMechanicType supportMechanicType = mechanicHelperService.getClass().getAnnotation(SupportMechanicType.class);
        return supportMechanicType.version();
    }
}
