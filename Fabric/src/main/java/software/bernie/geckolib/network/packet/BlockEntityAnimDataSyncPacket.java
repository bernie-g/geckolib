package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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
	private final BlockPos pos;
	private final SerializableDataTicket<D> dataTicket;
	private final D data;

	public BlockEntityAnimDataSyncPacket(BlockPos pos, SerializableDataTicket<D> dataTicket, D data) {
		this.pos = pos;
		this.dataTicket = dataTicket;
		this.data = data;
	}

	@Override
	public FriendlyByteBuf encode() {
		FriendlyByteBuf buf = PacketByteBufs.create();

		buf.writeBlockPos(this.pos);
		buf.writeUtf(this.dataTicket.id());
		this.dataTicket.encode(this.data, buf);

		return buf;
	}

	@Override
	public ResourceLocation getPacketID() {
		return GeckoLibNetwork.BLOCK_ENTITY_ANIM_DATA_SYNC_PACKET_ID;
	}

	public static <D> void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		final BlockPos pos = buf.readBlockPos();
		final SerializableDataTicket<D> dataTicket = (SerializableDataTicket<D>)DataTickets.byName(buf.readUtf());
		final D data = dataTicket.decode(buf);

		client.execute(() -> runOnThread(pos, dataTicket, data));
	}

	private static <D> void runOnThread(BlockPos blockPos, SerializableDataTicket<D> dataTicket, D data) {
		BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(blockPos);

		if (blockEntity instanceof GeoBlockEntity geoBlockEntity)
			geoBlockEntity.setAnimData(dataTicket, data);
	}
}
