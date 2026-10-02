package team.lodestar.lodestone.systems.rendering.builder.data;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public record CubeVertexData(CubeVertices bottomVertices, CubeVertices topVertices, List<CubeVertices> byHorizontalDirection) {

    public CubeVertexData(Vector3f[] bottomVertices, Vector3f[] topVertices, List<Vector3f[]> directionToVertex) {
        this(new CubeVertices(bottomVertices), new CubeVertices(topVertices), directionToVertex.stream().map(CubeVertices::new).toList());
    }

    public static CubeVertexData makeCubePositions(float scale) {
        return makeCubePositions(scale, scale);
    }

    public static CubeVertexData makeCubePositions(float hScale, float vScale) {
        float xOffset = hScale / 2f;
        float yOffset = vScale / 2f;
        return makeCubePositions(-xOffset, xOffset, -yOffset, yOffset);
    }

    public static CubeVertexData makeCubePositions(float hStart, float hEnd, float vStart, float vEnd) {
        return makeCubePositions(hStart, hEnd, vStart, vEnd, hStart, hEnd);
    }

    public static CubeVertexData makeCubePositions(float xStart, float xEnd, float yStart, float yEnd, float zStart, float zEnd) {
        Vector3f[] bottomVertices = new Vector3f[]{new Vector3f(xStart, yStart, zStart), new Vector3f(xStart, yStart, zEnd), new Vector3f(xEnd, yStart, zEnd), new Vector3f(xEnd, yStart, zStart)};
        Vector3f[] topVertices = new Vector3f[]{new Vector3f(xStart, yEnd, zStart), new Vector3f(xStart, yEnd, zEnd), new Vector3f(xEnd, yEnd, zEnd), new Vector3f(xEnd, yEnd, zStart)};
        List<Vector3f[]> byHorizontalDirection = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int index = (i * 3 + 3) % 4; //this weird and specific numbering is to tie the vertices to horizontal directions
            byHorizontalDirection.add(new Vector3f[]{bottomVertices[(index) % 4], bottomVertices[(index + 1) % 4], topVertices[(index + 1) % 4], topVertices[(index) % 4]});
        }
        return new CubeVertexData(bottomVertices, topVertices, byHorizontalDirection);
    }

    public Vector3f[] getVerticesByDirection(Direction direction) {
        if (direction.equals(Direction.UP)) {
            return topVertices.vertices;
        }
        if (direction.equals(Direction.DOWN)) {
            return bottomVertices.invert();
        }
        return getVerticesByIndex(direction.get2DDataValue()).vertices;
    }

    public CubeVertices getVerticesByIndex(int index) {
        return byHorizontalDirection.get(index);
    }

    //TODO: These kinda smell
    public CubeVertexData applyWobble(float sineOffset, float strength) {
        return applyWobble(sineOffset, sineOffset, strength);
    }

    public CubeVertexData applyWobble(float bottomSineOffset, float topSineOffset, float strength) {
        return applyWobble(bottomVertices, bottomSineOffset, strength).applyWobble(topVertices, topSineOffset, strength);
    }

    public CubeVertexData applyWobble(CubeVertices vertices, float sineOffset, float strength) {
        applyVertexWobble(vertices, sineOffset, strength);
        return this;
    }

    public static void applyVertexWobble(CubeVertices vertices, float sineOffset, float strength) {
        applyVertexWobble(vertices.vertices, sineOffset, strength);
    }

    public static void applyVertexWobble(Vector3f[] vertices, float sineOffset, float strength) {
        float offset = sineOffset;
        long gameTime = Minecraft.getInstance().level.getGameTime();
        for (Vector3f vertex : vertices) {
            double time = ((gameTime / 40.0F) % 40.0F) * 6.28f;
            float angle = (float) (time + (offset * 6.28f));
            float sin = Mth.sin(angle) * strength;
            float cos = Mth.cos(angle) * strength;
            vertex.add(sin, cos, 0);
            offset += 0.25f;
        }
    }

    public CubeVertexData scale(float scale) {
        return scale(scale, scale);
    }

    public CubeVertexData scale(float width, float height) {
        return scale(width, height, width);
    }

    public CubeVertexData scale(float x, float y, float z) {
        scale(CubeVertexData::bottomVertices, x, y, z);
        scale(CubeVertexData::topVertices, x, y, z);
        return this;
    }

    public CubeVertexData scale(Function<CubeVertexData, CubeVertices> vertices, float scale) {
        return scale(vertices, scale, scale);
    }

    public CubeVertexData scale(Function<CubeVertexData, CubeVertices> vertices, float width, float height) {
        return scale(vertices, width, height, width);
    }

    public CubeVertexData scale(Function<CubeVertexData, CubeVertices> vertices, float x, float y, float z) {
        vertices.apply(this).scale(x, y, z);
        return this;
    }

    public CubeVertexData offset(float x, float y, float z) {
        offset(CubeVertexData::bottomVertices, x, y, z);
        offset(CubeVertexData::topVertices, x, y, z);
        return this;
    }

    public CubeVertexData offset(Function<CubeVertexData, CubeVertices> vertices, float x, float y, float z) {
        vertices.apply(this).scale(x, y, z);
        return this;
    }

    public record CubeVertices(Vector3f[] vertices) {

        public CubeVertices scale(float scale) {
            return scale(scale, scale);
        }

        public CubeVertices scale(float width, float height) {
            return scale(width, height, width);
        }

        public CubeVertices scale(float x, float y, float z) {
            for (Vector3f vertex : vertices) {
                vertex.mul(x, y, z);
            }
            return this;
        }

        public CubeVertices offset(float x, float y, float z) {
            for (Vector3f vertex : vertices) {
                vertex.add(x, y, z);
            }
            return this;
        }

        public Vector3f[] invert() {
            return new Vector3f[]{vertices[3], vertices[2], vertices[1], vertices[0]};
        }
    }
}