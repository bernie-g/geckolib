package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animation data for
 * {@link net.minecraft.world.entity.Entity Entities}
 */
public class EntityAnimDataSyncPacket<D> extends AbstractPacket {
	private final int entityId;
	private final SerializableDataTicket<D> dataTicket;
	private final D data;

	public EntityAnimDataSyncPacket(int entityId, SerializableDataTicket<D> dataTicket, D data) {
		this.entityId = entityId;
		this.dataTicket = dataTicket;
		this.data = data;
	}

	@Override
	public FriendlyByteBuf encode() {
		FriendlyByteBuf buf = PacketByteBufs.create();

		buf.writeVarInt(this.entityId);
		buf.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buf);

		return buf;
	}

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.ENTITY_ANIM_DATA_SYNC_PACKET_ID;
	}

	public static <D> void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		final int entityId = buf.readVarInt();
		final SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>) DataTickets.byName(buf.readUtf());
		final D data = dataTicket.decode(buf);

		client.execute(() -> runOnThread(entityId, dataTicket, data));
	}

	private static <D> void runOnThread(int entityId, SerializableDataTicket<D> dataTicket, D data) {
		Entity entity = ClientUtils.getLevel().getEntity(entityId);

		if (entity instanceof GeoEntity geoEntity)
			geoEntity.setAnimData(dataTicket, data);
	}
}
