package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.stateless.StatelessGeoSingletonAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;

/**
 * Packet for instructing clients to set a stop state for an animation for a {@link StatelessGeoSingletonAnimatable}
 */
public class StatelessSingletonStopAnimPacket extends AbstractPacket {
	private final String syncableId;
	private final long instanceId;
	private final String animation;

	public StatelessSingletonStopAnimPacket(String syncableId, long instanceId, String animation) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.animation = animation;
	}

	@Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUtf(this.syncableId);
        buf.writeVarLong(this.instanceId);
        buf.writeUtf(this.animation);

        return buf;
    }

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.STATELESS_STOP_ANIM_PACKET_ID;
	}

	public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		final String syncableId = buf.readUtf();
		final long instanceID = buf.readVarLong();
		final String animation = buf.readUtf();

        client.execute(() -> runOnThread(syncableId, instanceID, animation));
    }

	private static <D> void runOnThread(String syncableId, long instanceId, String animation) {
		GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

        if (animatable instanceof StatelessGeoSingletonAnimatable statelessAnimatable)
            statelessAnimatable.handleClientAnimationStop(animatable, instanceId, animation);
	}
}
