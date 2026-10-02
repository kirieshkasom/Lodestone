package team.lodestar.lodestone.modules.rendering.model.obj.data;

import java.util.ArrayList;
import java.util.List;

public class ObjPart {
    private final String name;
    private final List<IndexedMesh> meshes = new ArrayList<>();

    public ObjPart(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<IndexedMesh> getMeshes() {
        return this.meshes;
    }

    public void addMesh(IndexedMesh mesh) {
        this.meshes.add(mesh);
    }
}