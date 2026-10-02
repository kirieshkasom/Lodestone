package team.lodestar.lodestone.modules.rendering.model.geo.data;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class GeoVertex {
    private Vector3f position;
    private Vector2f texCoord;

    public GeoVertex(Vector3f position, Vector2f texCoord) {
        this.position = position;
        this.texCoord = texCoord;
    }

    public GeoVertex(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
        this.texCoord = new Vector2f(0.0f, 0.0f);
    }

    public void setTexCoords(Vector2f texCoords) {
        this.texCoord.set(texCoords);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getTexCoord() {
        return texCoord;
    }

    public GeoVertex copy() {
        return new GeoVertex(new Vector3f(this.position), new Vector2f(this.texCoord));
    }
}
