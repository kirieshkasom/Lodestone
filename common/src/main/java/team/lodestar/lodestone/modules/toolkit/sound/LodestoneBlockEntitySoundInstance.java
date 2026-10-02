package team.lodestar.lodestone.modules.toolkit.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;

public class LodestoneBlockEntitySoundInstance<T extends LodestoneBlockEntity> extends AbstractTickableSoundInstance {

    public T blockEntity;

    public LodestoneBlockEntitySoundInstance(T blockEntity, SoundEvent soundEvent, float volume, float pitch) {
        super(soundEvent, SoundSource.BLOCKS, blockEntity.getLevel().getRandom());
        this.blockEntity = blockEntity;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = 0;
        this.looping = true;
    }

    @Override
    public void tick() {
        if (blockEntity.isRemoved()) {
            stop();
        }
    }
}
