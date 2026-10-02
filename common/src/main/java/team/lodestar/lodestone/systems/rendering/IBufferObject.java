package team.lodestar.lodestone.systems.rendering;

/**
 * Utility interface for registering buffer objects to be destroyed when the game closes.
 */
public interface IBufferObject {
    default void registerBufferObject() {
        LodestoneRenderSystem.registerBufferObject(this);
    }
    void destroy();
}
