package team.lodestar.lodestone.compability;

import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.handlers.*;
import team.lodestar.lodestone.helpers.*;

public class CuriosCompat {
    public static boolean LOADED;

    public static void init(boolean loaded) {
        LOADED = loaded;
        if (LOADED) {
            LoadedOnly.init();
        }
    }

    public static class LoadedOnly {

        public static final ItemEventHandler.EventResponderSource CURIOS = new ItemEventHandler.EventResponderSource(LodestoneCommon.lodestonePath("curios"), CurioHelper::getEquippedCurios);

        public static void init() {
            ItemEventHandler.registerLookup(CURIOS);
        }
    }
}