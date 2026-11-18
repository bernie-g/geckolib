package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Packet for syncing user-definable animation data for
 * {@link net.minecraft.world.entity.Entity Entities}
 */
public class EntityAnimDataSyncPacket<D> extends AbstractPacket {
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

	@Override
	public FriendlyByteBuf encode() {
		FriendlyByteBuf buffer = PacketByteBufs.create();

		buffer.writeVarInt(this.entityId);
		buffer.writeBoolean(this.isReplacedEntity);
		buffer.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buffer);

		return buffer;
	}

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.ENTITY_ANIM_DATA_SYNC_PACKET_ID;
	}

	public static <D> void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender responseSender) {
		final int entityId = buffer.readVarInt();
		final boolean isReplacedEntity = buffer.readBoolean();
		final SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>) DataTickets.byName(buffer.readUtf());
		final D data = dataTicket.decode(buffer);

		client.execute(() -> runOnThread(entityId, isReplacedEntity, dataTicket, data));
	}

	private static <D> void runOnThread(int entityId, boolean isReplacedEntity, SerializableDataTicket<D> dataTicket, D data) {
		Entity entity = ClientUtils.getLevel().getEntity(entityId);

		if (entity == null)
			return;

		if (isReplacedEntity) {
			GeoAnimatable animatable = RenderUtils.getReplacedAnimatable(entity.getType());

			if (animatable instanceof GeoReplacedEntity replacedEntity)
				replacedEntity.setAnimData(entity, dataTicket, data);
		}
		else if (entity instanceof GeoEntity geoEntity) {
			geoEntity.setAnimData(dataTicket, data);
		}
	}
}
