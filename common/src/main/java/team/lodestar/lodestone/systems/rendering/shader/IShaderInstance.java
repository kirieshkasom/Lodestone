package team.lodestar.lodestone.systems.rendering.shader;

import javax.annotation.Nullable;

public interface IShaderInstance {
    @Nullable
    LodestoneProgram getGeometryProgram();
}
