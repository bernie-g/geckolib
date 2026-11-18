package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Supplier;

/**
 * Packet for syncing user-definable animation data for {@link net.minecraft.world.entity.Entity Entities}
 */
public class EntityAnimDataSyncPacket<D> {
	private final int entityId;
	private final boolean isReplacedEntity;
	private final SerializableDataTicket<D> dataTicket;
	private final D data;

	public EntityAnimDataSyncPacket(int entityId, SerializableDataTicket<D> dataTicket, D data) {
		this(entityId, false, dataTicket, data);
	}

	public EntityAnimDataSyncPacket(int entityId, boolean isReplacedEntity, SerializableDataTicket<D> dataTicket, D data) {
		this.entityId = entityId;
		this.isReplacedEntity = isReplacedEntity;
		this.dataTicket = dataTicket;
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.entityId);
		buffer.writeBoolean(this.isReplacedEntity);
		buffer.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buffer);
	}

	public static <D> EntityAnimDataSyncPacket<D> decode(FriendlyByteBuf buffer) {
		int entityId = buffer.readVarInt();
		boolean isReplacedEntity = buffer.readBoolean();
		SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>)DataTickets.byName(buffer.readUtf());
		D data = dataTicket.decode(buffer);

		return new EntityAnimDataSyncPacket<>(entityId, isReplacedEntity, dataTicket, data);
	}

	public void receivePacket(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context handler = context.get();

		handler.enqueueWork(() -> {
			Entity entity = ClientUtils.getLevel().getEntity(this.entityId);

			if (entity == null)
				return;

			if (this.isReplacedEntity) {
				GeoAnimatable animatable = RenderUtils.getReplacedAnimatable(entity.getType());

				if (animatable instanceof GeoReplacedEntity replacedEntity)
					replacedEntity.setAnimData(entity, this.dataTicket, this.data);
			}
			else if (entity instanceof GeoEntity geoEntity) {
				geoEntity.setAnimData(this.dataTicket, this.data);
			}
		});
		handler.setPacketHandled(true);
	}
}
