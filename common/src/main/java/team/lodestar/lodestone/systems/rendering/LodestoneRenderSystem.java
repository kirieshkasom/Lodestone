package team.lodestar.lodestone.systems.rendering;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL43.*;

public class LodestoneRenderSystem extends RenderSystem {
    private static final List<IBufferObject> bufferObjects = new ArrayList<>();

    private static final Vector3f viewBobOffset = new Vector3f();


    public static void wrap(RenderCall renderCall) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(renderCall);
        } else {
            renderCall.execute();
        }
    }

    public static void registerBufferObject(IBufferObject bufferObject) {
        bufferObjects.add(bufferObject);
    }

    public static void unregisterBufferObject(IBufferObject bufferObject) {
        bufferObjects.remove(bufferObject);
    }

    public static void destroyBufferObjects() {
        Iterator<IBufferObject> objects = bufferObjects.iterator();
        while (objects.hasNext()) {
            IBufferObject object = objects.next();
            object.destroy();
            objects.remove();
        }
    }

    public static void dispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        wrap(() -> glDispatchCompute(num_groups_x, num_groups_y, num_groups_z));
    }

    public static void memoryBarrier(int barriers) {
        wrap(() -> glMemoryBarrier(barriers));
    }

    public static void bindBufferBase(int target, int index, int buffer) {
        wrap(() -> glBindBufferBase(target, index, buffer));
    }

    public static void mapBuffer(int target, int access, Consumer<ByteBuffer> byteBufferConsumer) {
        wrap(() -> byteBufferConsumer.accept(glMapBuffer(target, access)));
    }

    public static void unmapBuffer(int target) {
        wrap(() -> glUnmapBuffer(target));
    }

    public static void setViewBobOffset(float x, float y, float z) {
        LodestoneRenderSystem.viewBobOffset.set(x, y, z);
    }

    public static Vector3fc getViewBobOffset() {
        return LodestoneRenderSystem.viewBobOffset;
    }
}
