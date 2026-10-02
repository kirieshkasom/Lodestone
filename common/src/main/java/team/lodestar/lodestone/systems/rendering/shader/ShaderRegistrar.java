package team.lodestar.lodestone.systems.rendering.shader;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.Objects;
import java.util.function.Consumer;

/** Registers a compiled shader instance with the active loader's renderer. */
public final class ShaderRegistrar {
    private final ResourceProvider resources;
    private final ShaderInstanceRegistration registration;

    public ShaderRegistrar(ResourceProvider resources, ShaderInstanceRegistration registration) {
        this.resources = Objects.requireNonNull(resources);
        this.registration = Objects.requireNonNull(registration);
    }

    public ResourceProvider resources() {
        return resources;
    }

    public void register(ShaderInstance shaderInstance, Consumer<ShaderInstance> onReload) {
        registration.register(shaderInstance, onReload);
    }

    @FunctionalInterface
    public interface ShaderInstanceRegistration {
        void register(ShaderInstance shaderInstance, Consumer<ShaderInstance> onReload);
    }
}
