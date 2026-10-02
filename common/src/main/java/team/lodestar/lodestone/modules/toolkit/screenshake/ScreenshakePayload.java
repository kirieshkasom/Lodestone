package team.lodestar.lodestone.modules.toolkit.screenshake;

import net.minecraft.network.RegistryFriendlyByteBuf;
import team.lodestar.lodestone.internal.network.PayloadContext;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;

import java.util.function.*;

public class ScreenshakePayload extends OneSidedPayloadData {

    public final ScreenshakeInstance instance;

    public ScreenshakePayload(RegistryFriendlyByteBuf byteBuf) {
        this(ScreenshakeInstance.STREAM_CODEC.decode(byteBuf));
    }

    public ScreenshakePayload(Consumer<ScreenshakeBuilder> constructor) {
        ScreenshakeBuilder builder = ScreenshakeBuilder.create();
        constructor.accept(builder);
        this.instance = builder.build();
    }

    public ScreenshakePayload(ScreenshakeInstance instance) {
        this.instance = instance;
    }

    @Override
    public void handle(PayloadContext context) {
        context.execute(() -> ScreenshakeHandler.addScreenshake(instance));
    }

    @Override
    public void serialize(RegistryFriendlyByteBuf byteBuf) {
        ScreenshakeInstance.STREAM_CODEC.encode(byteBuf, instance);
    }
}
