package software.bernie.geckolib.network.packet;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;

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

	private final String SYNCABLE_ID;
	private final long INSTANCE_ID;
	private final SerializableDataTicket<D> DATA_TICKET;
	private final D DATA;

	public AnimDataSyncPacket(String syncableId, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
		this.SYNCABLE_ID = syncableId;
		this.INSTANCE_ID = instanceId;
		this.DATA_TICKET = dataTicket;
		this.DATA = data;
	}

	@Override
    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(this.SYNCABLE_ID);

        buf.writeVarLong(this.INSTANCE_ID);
        buf.writeUtf(this.DATA_TICKET.id());

        this.DATA_TICKET.encode(this.DATA, buf);
        return buf;
    }

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.ANIM_DATA_SYNC_PACKET_ID;
	}

	public static <D> void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		final String SYNCABLE_ID = buf.readUtf();
		final long INSTANCE_ID = buf.readVarLong();

		final SerializableDataTicket<D> DATA_TICKET = (SerializableDataTicket<D>) DataTickets.byName(buf.readUtf());
		final D DATA = DATA_TICKET.decode(buf);

        client.execute(() -> runOnThread(SYNCABLE_ID, INSTANCE_ID, DATA_TICKET, DATA));
    }

	private static <D> void runOnThread(String syncableId, long instanceId, SerializableDataTicket<D> dataTicket, D data) {
		GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

		if (animatable instanceof SingletonGeoAnimatable singleton)
			singleton.setAnimData(ClientUtils.getClientPlayer(), instanceId, dataTicket, data);
	}
}
