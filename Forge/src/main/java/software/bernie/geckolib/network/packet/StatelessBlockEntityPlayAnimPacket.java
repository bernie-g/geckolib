package software.bernie.geckolib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.stateless.StatelessGeoBlockEntity;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.NetworkUtil;

import java.util.function.Supplier;

/**
 * Packet for instructing clients to set a play state for an animation for a {@link StatelessGeoBlockEntity}
 */
public class StatelessBlockEntityPlayAnimPacket {
	private final BlockPos blockPos;
	private final RawAnimation animation;

	public StatelessBlockEntityPlayAnimPacket(BlockPos blockPos, RawAnimation animation) {
		this.blockPos = blockPos;
		this.animation = animation;
	}

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.blockPos);
        NetworkUtil.writeRawAnimationToBuffer(animation, buffer);
    }

    public static StatelessBlockEntityPlayAnimPacket decode(FriendlyByteBuf buffer) {
        BlockPos blockPos = buffer.readBlockPos();
        RawAnimation animation = NetworkUtil.readRawAnimationFromBuffer(buffer);

        return new StatelessBlockEntityPlayAnimPacket(blockPos, animation);
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();

        handler.enqueueWork(() -> {
            if (ClientUtils.getLevel().getBlockEntity(blockPos) instanceof GeoBlockEntity blockEntity && blockEntity instanceof StatelessGeoBlockEntity statelessAnimatable)
                statelessAnimatable.handleClientAnimationPlay(blockEntity, 0, animation);
        });
    }
}
