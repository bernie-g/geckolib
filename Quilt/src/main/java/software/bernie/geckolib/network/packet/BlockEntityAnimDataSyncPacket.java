package software.bernie.geckolib.network.packet;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animation data for {@link BlockEntity
 * BlockEntities}
 */
public class BlockEntityAnimDataSyncPacket<D> extends AbstractPacket {
	private final BlockPos BLOCK_POS;
	private final SerializableDataTicket<D> DATA_TICKET;
	private final D DATA;

	public BlockEntityAnimDataSyncPacket(BlockPos pos, SerializableDataTicket<D> dataTicket, D data) {
		this.BLOCK_POS = pos;
		this.DATA_TICKET = dataTicket;
		this.DATA = data;
	}

	@Override
	public FriendlyByteBuf encode() {
		FriendlyByteBuf buf = PacketByteBufs.create();

		buf.writeBlockPos(this.BLOCK_POS);
		buf.writeUtf(this.DATA_TICKET.id());
		this.DATA_TICKET.encode(this.DATA, buf);

		return buf;
	}

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID;
	}

	public static <D> void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		final BlockPos BLOCK_POS = buf.readBlockPos();
		final SerializableDataTicket<D> DATA_TICKET = (SerializableDataTicket<D>) DataTickets.byName(buf.readUtf());
		final D DATA = DATA_TICKET.decode(buf);

		client.execute(() -> runOnThread(BLOCK_POS, DATA_TICKET, DATA));
	}

	private static <D> void runOnThread(BlockPos blockPos, SerializableDataTicket<D> dataTicket, D data) {
		BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(blockPos);

		if (blockEntity instanceof GeoBlockEntity geoBlockEntity)
			geoBlockEntity.setAnimData(dataTicket, data);
	}
}
