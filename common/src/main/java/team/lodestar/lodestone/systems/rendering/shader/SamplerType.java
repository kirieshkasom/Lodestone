package team.lodestar.lodestone.systems.rendering.shader;

import static org.lwjgl.opengl.GL20.*;

public enum SamplerType {
    TEXTURE_1D("1d", GL_TEXTURE_1D),
    TEXTURE_2D("2d", GL_TEXTURE_2D),
    TEXTURE_3D("3d", GL_TEXTURE_3D),
    TEXTURE_CUBE("cube", GL_TEXTURE_CUBE_MAP);

    final String type;
    final int glType;

    SamplerType(String type, int glType) {
        this.type = type;
        this.glType = glType;
    }

    public int getGlType() {
        return glType;
    }

    public static SamplerType fromString(String type) {
        for (SamplerType samplerType : values()) {
            if (samplerType.type.equalsIgnoreCase(type)) {
                return samplerType;
            }
        }
        return null;
    }

    public static SamplerType getDefault() {
        return TEXTURE_2D;
    }

}
