package team.lodestar.lodestone.handlers;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import team.lodestar.lodestone.modules.core.easing.*;
import team.lodestar.lodestone.modules.toolkit.screenshake.*;

import java.util.*;
import java.util.function.*;

public class ScreenshakeHandler {

    private static final ArrayList<ScreenshakeInstance> INSTANCES = new ArrayList<>();

    private static float intensity;

    public static void applyCameraJitter(RandomSource random, CameraAngles angles) {
        if (intensity > 0) {
            float yaw = Easing.SINE_IN_OUT.asWeighedRandom(random, 0, intensity * 2) * (random.nextBoolean() ? 1 : -1);
            float pitch = Easing.SINE_IN_OUT.asWeighedRandom(random, 0, intensity * 2) * (random.nextBoolean() ? 1 : -1);
            angles.setYaw(angles.getYaw() + yaw);
            angles.setPitch(angles.getPitch() + pitch);
        }
    }

    public interface CameraAngles {
        float getYaw();

        float getPitch();

        void setYaw(float yaw);

        void setPitch(float pitch);
    }

    public static void clientTick(ClientLevel level, Camera camera) {
        float intensitySum = 0;
        for (ScreenshakeInstance instance : INSTANCES) {
            instance.tick();
            intensitySum += instance.getStrength(camera);
        }
        intensity = intensitySum;
        INSTANCES.removeIf(ScreenshakeInstance::isExpired);
    }

    public static void addScreenshake(Consumer<ScreenshakeBuilder> constructor) {
        ScreenshakeBuilder builder = ScreenshakeBuilder.create();
        constructor.accept(builder);
        addScreenshake(builder.build());
    }

    public static void addScreenshake(ScreenshakeInstance instance) {
        INSTANCES.add(instance);
    }
}
