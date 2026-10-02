package team.lodestar.lodestone.modules.core.datagen;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class DatagenContext {
    private static volatile BooleanSupplier datagenState = () -> false;

    private DatagenContext() {
    }

    public static boolean isDatagenRunning() {
        return datagenState.getAsBoolean();
    }

    public static void setDatagenRunning(boolean running) {
        datagenState = () -> running;
    }

    public static void setDatagenState(BooleanSupplier state) {
        datagenState = Objects.requireNonNull(state);
    }
}
