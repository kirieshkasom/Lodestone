package team.lodestar.lodestone.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;

@Mixin(Camera.class)
public abstract class CameraScreenshakeMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "setup", at = @At("TAIL"))
    private void lodestone$applyScreenshake(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            Camera camera = (Camera) (Object) this;
            float[] angles = {camera.getYRot(), camera.getXRot()};
            ScreenshakeHandler.applyCameraJitter(minecraft.level.getRandom(), new ScreenshakeHandler.CameraAngles() {
                @Override
                public float getYaw() {
                    return angles[0];
                }

                @Override
                public float getPitch() {
                    return angles[1];
                }

                @Override
                public void setYaw(float yaw) {
                    angles[0] = yaw;
                }

                @Override
                public void setPitch(float pitch) {
                    angles[1] = pitch;
                }
            });
            setRotation(angles[0], angles[1]);
        }
    }
}
