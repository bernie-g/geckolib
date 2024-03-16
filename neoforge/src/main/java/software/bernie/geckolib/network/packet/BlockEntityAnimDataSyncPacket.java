package software.bernie.geckolib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animation data for {@link BlockEntity BlockEntities}
 */
public record BlockEntityAnimDataSyncPacket<D>(BlockPos pos, SerializableDataTicket<D> dataTicket, D data) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(GeckoLibConstants.MODID, "block_entity_anim_data_sync");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buffer);
	}

	public static <D> BlockEntityAnimDataSyncPacket<D> decode(FriendlyByteBuf buffer) {
		BlockPos pos = buffer.readBlockPos();
		SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>)DataTickets.byName(buffer.readUtf());

		return new BlockEntityAnimDataSyncPacket<>(pos, dataTicket, dataTicket.decode(buffer));
	}

	public void receivePacket(PlayPayloadContext context) {
		context.workHandler().execute(() -> {
			BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(this.pos);

			if (blockEntity instanceof GeoBlockEntity geoBlockEntity)
				geoBlockEntity.setAnimData(this.dataTicket, this.data);
		});
	}
}
