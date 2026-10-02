package team.lodestar.lodestone.modules.datagen.providers;

import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ResourceFileHelper {
    private final List<Path> roots;
    private final Set<ResourceLocation> generated = new HashSet<>();

    public ResourceFileHelper(Collection<Path> roots) {
        this.roots = List.copyOf(roots);
    }

    public static ResourceFileHelper empty() {
        return new ResourceFileHelper(List.of());
    }

    public void trackGenerated(ResourceLocation location) {
        generated.add(location);
    }

    public boolean exists(ResourceLocation location, PackType packType, String extension, String directory) {
        if (generated.contains(location)) {
            return true;
        }
        String packDirectory = packType == PackType.CLIENT_RESOURCES ? "assets" : "data";
        String relativePath = packDirectory + "/" + location.getNamespace() + "/" + directory + "/" + location.getPath() + extension;
        for (Path root : roots) {
            if (Files.exists(root.resolve(relativePath))) {
                return true;
            }
        }
        return false;
    }
}
