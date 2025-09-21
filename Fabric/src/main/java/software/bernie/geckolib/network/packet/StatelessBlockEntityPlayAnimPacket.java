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
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.NetworkUtil;

/**
 * Packet for instructing clients to set a play state for an animation for a {@link StatelessGeoBlockEntity}
 */
public class StatelessBlockEntityPlayAnimPacket extends AbstractPacket {
	private final BlockPos blockPos;
	private final RawAnimation animation;

	public StatelessBlockEntityPlayAnimPacket(BlockPos blockPos, RawAnimation animation) {
		this.blockPos = blockPos;
		this.animation = animation;
	}

	@Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(this.blockPos);
        NetworkUtil.writeRawAnimationToBuffer(animation, buf);

        return buf;
    }

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.STATELESS_BLOCK_ENTITY_PLAY_ANIM_PACKET_ID;
	}

	public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		BlockPos blockPos = buf.readBlockPos();
		RawAnimation animation = NetworkUtil.readRawAnimationFromBuffer(buf);

        client.execute(() -> runOnThread(blockPos, animation));
    }

	private static <D> void runOnThread(BlockPos blockPos, RawAnimation animation) {
        if (ClientUtils.getLevel().getBlockEntity(blockPos) instanceof GeoBlockEntity blockEntity && blockEntity instanceof StatelessGeoBlockEntity statelessAnimatable)
            statelessAnimatable.handleClientAnimationPlay(blockEntity, 0, animation);
	}
}
