package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animation data for
 * {@link SingletonGeoAnimatable} instances
 */
public class AnimDataSyncPacket<D> extends AbstractPacket {
	private final String syncableId;
	private final long instanceId;
	private final SerializableDataTicket<D> dataTicket;
	private final D data;

	public AnimDataSyncPacket(String syncableId, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.dataTicket = dataTicket;
		this.data = data;
	}

	@Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUtf(this.syncableId);
        buf.writeVarLong(this.instanceId);
        buf.writeUtf(this.dataTicket.id());
        this.dataTicket.encode(this.data, buf);

        return buf;
    }

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.ANIM_DATA_SYNC_PACKET_ID;
	}

	public static <D> void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		String syncableId = buf.readUtf();
		long instanceID = buf.readVarLong();
		SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>)DataTickets.byName(buf.readUtf());
		D data = dataTicket.decode(buf);

        client.execute(() -> runOnThread(syncableId, instanceID, dataTicket, data));
    }

	private static <D> void runOnThread(String syncableId, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
		GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

		if (animatable instanceof SingletonGeoAnimatable singleton)
			singleton.setAnimData(ClientUtils.getClientPlayer(), instanceId, dataTicket, data);
	}
}
