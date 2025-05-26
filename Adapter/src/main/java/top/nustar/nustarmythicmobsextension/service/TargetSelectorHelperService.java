package top.nustar.nustarmythicmobsextension.service;

import top.nustar.nustarmythicmobsextension.adapter.impl.NuStarTargerSelector;
import top.nustar.nustarmythicmobsextension.service.annotations.SupportTargetSelectorType;
import top.nustar.nustarmythicmobsextension.service.enums.MythicMobsVersion;
import top.nustar.nustarmythicmobsextension.service.enums.TargetSelectorType;

public interface TargetSelectorHelperService {
    NuStarTargerSelector findSelector(Object... objects);

    default TargetSelectorType findSelectorTypeFromService(TargetSelectorHelperService targetSelectorHelperService) {
        SupportTargetSelectorType supportTargetSelectorType = targetSelectorHelperService.getClass().getAnnotation(SupportTargetSelectorType.class);
        return supportTargetSelectorType.type();
    }

    default MythicMobsVersion findMythicMobsVersionFromService(TargetSelectorHelperService targetSelectorHelperService) {
        SupportTargetSelectorType supportTargetSelectorType = targetSelectorHelperService.getClass().getAnnotation(SupportTargetSelectorType.class);
        return supportTargetSelectorType.version();
    }
}
