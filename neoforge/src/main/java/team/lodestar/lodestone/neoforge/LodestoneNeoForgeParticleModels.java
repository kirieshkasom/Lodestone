package team.lodestar.lodestone.neoforge;

import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import team.lodestar.lodestone.internal.client.ParticleModelAccess;

public final class LodestoneNeoForgeParticleModels {
    private LodestoneNeoForgeParticleModels() {
    }

    public static void install() {
        ParticleModelAccess.install(model -> model.getParticleIcon(ModelData.EMPTY),
                (state, level, pos) -> IClientBlockExtensions.of(state).areBreakingParticlesTinted(state, level, pos));
    }
}
