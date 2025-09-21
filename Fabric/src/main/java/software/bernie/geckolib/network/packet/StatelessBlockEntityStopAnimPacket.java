package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.stateless.StatelessGeoBlockEntity;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for instructing clients to set a stop state for an animation for a {@link StatelessGeoBlockEntity}
 */
public class StatelessBlockEntityStopAnimPacket extends AbstractPacket {
	private final BlockPos blockPos;
	private final String animation;

	public StatelessBlockEntityStopAnimPacket(BlockPos blockPos, String animation) {
		this.blockPos = blockPos;
		this.animation = animation;
	}

	@Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(this.blockPos);
        buf.writeUtf(this.animation);

        return buf;
    }

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.STATELESS_BLOCK_ENTITY_STOP_ANIM_PACKET_ID;
	}

	public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		BlockPos blockPos = buf.readBlockPos();
		String animation = buf.readUtf();

        client.execute(() -> runOnThread(blockPos, animation));
    }

	private static <D> void runOnThread(BlockPos blockPos, String animation) {
        if (ClientUtils.getLevel().getBlockEntity(blockPos) instanceof GeoBlockEntity blockEntity && blockEntity instanceof StatelessGeoBlockEntity statelessAnimatable)
            statelessAnimatable.handleClientAnimationStop(blockEntity, 0, animation);
	}
}
