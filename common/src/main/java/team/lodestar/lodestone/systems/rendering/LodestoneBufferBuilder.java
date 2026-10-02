package team.lodestar.lodestone.systems.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.lwjgl.system.MemoryUtil;

public class LodestoneBufferBuilder {
    private BufferBuilder parent;
    private long address;

    public LodestoneBufferBuilder(VertexConsumer parent) {
        if (parent instanceof BufferBuilder) {
            this.parent = (BufferBuilder) parent;
        } else {
            throw new IllegalArgumentException("Parent must be a BufferBuilder");
        }
    }

    public LodestoneBufferBuilder beginElement(VertexFormatElement element) {
            this.address = parent.beginElement(element);
            return this;
    }

    public LodestoneBufferBuilder putFloats(float... data) {
        if (verify()) {
            for (int i = 0; i < data.length; i++) {
                MemoryUtil.memPutFloat(this.address + (i * Float.BYTES), data[i]);
            }
        }
        return this;
    }
    public LodestoneBufferBuilder putInts(int... data) {
        if (verify()) {
            for (int i = 0; i < data.length; i++) {
                MemoryUtil.memPutInt(this.address + (i * Integer.BYTES), data[i]);
            }
        }
        return this;
    }
    public LodestoneBufferBuilder putShorts(short... data) {
        if (verify()) {
            for (int i = 0; i < data.length; i++) {
                MemoryUtil.memPutShort(this.address + (i * Short.BYTES), data[i]);
            }
        }
        return this;
    }
    public LodestoneBufferBuilder putBytes(byte... data) {
        if (verify()) {
            for (int i = 0; i < data.length; i++) {
                MemoryUtil.memPutByte(this.address + i, data[i]);
            }
        }
        return this;
    }


    private boolean verify() {
        return this.address != -1L;
    }
}
