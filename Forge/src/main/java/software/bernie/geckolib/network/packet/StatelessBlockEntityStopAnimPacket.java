package software.bernie.geckolib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.stateless.StatelessGeoBlockEntity;
import software.bernie.geckolib.util.ClientUtils;

import java.util.function.Supplier;

/**
 * Packet for instructing clients to set a stop state for an animation for a {@link StatelessGeoBlockEntity}
 */
public class StatelessBlockEntityStopAnimPacket {
	private final BlockPos blockPos;
	private final String animation;

	public StatelessBlockEntityStopAnimPacket(BlockPos blockPos, String animation) {
		this.blockPos = blockPos;
		this.animation = animation;
	}

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.blockPos);
        buffer.writeUtf(this.animation);
    }

    public static StatelessBlockEntityStopAnimPacket decode(FriendlyByteBuf buffer) {
        BlockPos blockPos = buffer.readBlockPos();
        String animation = buffer.readUtf();

        return new StatelessBlockEntityStopAnimPacket(blockPos, animation);
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();

        handler.enqueueWork(() -> {
            if (ClientUtils.getLevel().getBlockEntity(blockPos) instanceof GeoBlockEntity blockEntity && blockEntity instanceof StatelessGeoBlockEntity statelessAnimatable)
                statelessAnimatable.handleClientAnimationStop(blockEntity, 0, animation);
        });
    }
}
