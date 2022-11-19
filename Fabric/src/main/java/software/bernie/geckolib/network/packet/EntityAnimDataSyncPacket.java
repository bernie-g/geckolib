package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animation data for
 * {@link net.minecraft.world.entity.Entity Entities}
 */
public class EntityAnimDataSyncPacket<D> {
	private final int entityId;
	private final SerializableDataTicket<D> dataTicket;
	private final D data;

	public EntityAnimDataSyncPacket(int entityId, SerializableDataTicket<D> dataTicket, D data) {
		this.entityId = entityId;
		this.dataTicket = dataTicket;
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.entityId);
		buffer.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buffer);
	}

	public static <D> EntityAnimDataSyncPacket<D> decode(FriendlyByteBuf buffer) {
		int entityId = buffer.readVarInt();
		SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>) DataTickets.byName(buffer.readUtf());

		return new EntityAnimDataSyncPacket<>(entityId, dataTicket, dataTicket.decode(buffer));
	}

	public void receivePacket() {
		Entity entity = ClientUtils.getLevel().getEntity(this.entityId);

		if (entity instanceof GeoEntity geoEntity)
			geoEntity.setAnimData(this.dataTicket, this.data);
	}
}
