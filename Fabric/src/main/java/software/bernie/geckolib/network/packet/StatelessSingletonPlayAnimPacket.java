package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.stateless.StatelessGeoSingletonAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.NetworkUtil;

/**
 * Packet for instructing clients to set a play state for an animation for a {@link StatelessGeoSingletonAnimatable}
 */
public class StatelessSingletonPlayAnimPacket extends AbstractPacket {
	private final String syncableId;
	private final long instanceId;
	private final RawAnimation animation;

	public StatelessSingletonPlayAnimPacket(String syncableId, long instanceId, RawAnimation animation) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.animation = animation;
	}

	@Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUtf(this.syncableId);
        buf.writeVarLong(this.instanceId);
        NetworkUtil.writeRawAnimationToBuffer(animation, buf);

        return buf;
    }

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.STATELESS_PLAY_ANIM_PACKET_ID;
	}

	public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		final String syncableId = buf.readUtf();
		final long instanceID = buf.readVarLong();
		final RawAnimation animation = NetworkUtil.readRawAnimationFromBuffer(buf);

        client.execute(() -> runOnThread(syncableId, instanceID, animation));
    }

	private static <D> void runOnThread(String syncableId, long instanceId, RawAnimation animation) {
		GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

        if (animatable instanceof StatelessGeoSingletonAnimatable statelessAnimatable)
            statelessAnimatable.handleClientAnimationPlay(animatable, instanceId, animation);
	}
}
