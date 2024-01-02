package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animation data for {@link net.minecraft.world.entity.Entity Entities}
 */
public record EntityAnimDataSyncPacket<D>(int entityId, SerializableDataTicket<D> dataTicket, D data) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(GeckoLib.MOD_ID, "entity_anim_data_sync");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.entityId);
		buffer.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buffer);
	}

	public static <D> EntityAnimDataSyncPacket<D> decode(FriendlyByteBuf buffer) {
		int entityId = buffer.readVarInt();
		SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>)DataTickets.byName(buffer.readUtf());

		return new EntityAnimDataSyncPacket<>(entityId, dataTicket, dataTicket.decode(buffer));
	}

	public void receivePacket(PlayPayloadContext context) {
		context.workHandler().execute(() -> {
			Entity entity = ClientUtils.getLevel().getEntity(this.entityId);

			if (entity instanceof GeoEntity geoEntity)
				geoEntity.setAnimData(this.dataTicket, this.data);
		});
	}
}
