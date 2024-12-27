package software.bernie.geckolib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.util.ClientUtils;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Packet for syncing user-definable animations that can be triggered from the server for {@link BlockEntity BlockEntities}
 */
public class StopTriggeredBlockEntityAnimPacket<D> {
	private final BlockPos pos;
	private final String controllerName;
	private final String animName;

	public StopTriggeredBlockEntityAnimPacket(BlockPos pos, @Nullable String controllerName, @Nullable String animName) {
		this.pos = pos;
		this.controllerName = controllerName == null ? "" : controllerName;
		this.animName = animName == null ? "" : animName;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeUtf(this.controllerName);
		buffer.writeUtf(this.animName);
	}

	public static <D> StopTriggeredBlockEntityAnimPacket<D> decode(FriendlyByteBuf buffer) {
		return new StopTriggeredBlockEntityAnimPacket<>(buffer.readBlockPos(), buffer.readUtf(), buffer.readUtf());
	}

	public void receivePacket(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context handler = context.get();

		handler.enqueueWork(() -> {
			BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(this.pos);

			if (blockEntity instanceof GeoBlockEntity getBlockEntity)
				getBlockEntity.stopTriggeredAnimation(this.controllerName.isEmpty() ? null : this.controllerName, this.animName.isEmpty() ? null : this.animName);
		});
		handler.setPacketHandled(true);
	}
}
