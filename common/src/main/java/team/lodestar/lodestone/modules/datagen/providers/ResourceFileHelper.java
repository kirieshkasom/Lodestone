package team.lodestar.lodestone.modules.datagen.providers;

import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/** Checks resources in supplied pack roots, the classpath, and tracked generated models. */
public final class ResourceFileHelper {
    private final List<Path> roots;
    private final Predicate<String> resources;
    private final Set<ResourceLocation> generatedModels = new HashSet<>();

    public ResourceFileHelper(Collection<Path> roots) {
        this(roots, path -> ResourceFileHelper.class.getClassLoader().getResource(path) != null);
    }

    /** The predicate receives complete pack paths such as assets/example/models/item/tool.json. */
    public ResourceFileHelper(Collection<Path> roots, Predicate<String> resources) {
        this.roots = List.copyOf(roots);
        this.resources = Objects.requireNonNull(resources);
    }

    public static ResourceFileHelper empty() {
        return new ResourceFileHelper(List.of());
    }

    public void trackGenerated(ResourceLocation location) {
        generatedModels.add(location);
    }

    public boolean exists(ResourceLocation location, PackType packType, String extension, String directory) {
        if (packType == PackType.CLIENT_RESOURCES && directory.equals("models") && extension.equals(".json") && generatedModels.contains(location)) {
            return true;
        }
        String packDirectory = packType == PackType.CLIENT_RESOURCES ? "assets" : "data";
        String relativePath = packDirectory + "/" + location.getNamespace() + "/" + directory + "/" + location.getPath() + extension;
        for (Path root : roots) {
            if (Files.isRegularFile(root.resolve(relativePath))) {
                return true;
            }
        }
        return resources.test(relativePath);
    }

    public void requireModel(ResourceLocation location) {
        if (!exists(location, PackType.CLIENT_RESOURCES, ".json", "models")) {
            throw new IllegalStateException("Missing model: " + location);
        }
    }

    public void requireTexture(ResourceLocation location) {
        if (!exists(location, PackType.CLIENT_RESOURCES, ".png", "textures")) {
            throw new IllegalStateException("Missing texture: " + location);
        }
    }
}
