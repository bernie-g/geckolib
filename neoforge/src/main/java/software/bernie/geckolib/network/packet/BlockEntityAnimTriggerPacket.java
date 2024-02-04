package software.bernie.geckolib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animations that can be triggered from the server for {@link net.minecraft.world.level.block.entity.BlockEntity BlockEntities}
 */
public record BlockEntityAnimTriggerPacket<D>(BlockPos pos, String controllerName, String animName) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(GeckoLibConstants.MODID, "block_entity_anim_trigger");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeUtf(this.controllerName);
		buffer.writeUtf(this.animName);
	}

	public static <D> BlockEntityAnimTriggerPacket<D> decode(FriendlyByteBuf buffer) {
		return new BlockEntityAnimTriggerPacket<>(buffer.readBlockPos(), buffer.readUtf(), buffer.readUtf());
	}

	public void receivePacket(PlayPayloadContext context) {
		context.workHandler().execute(() -> {
			BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(this.pos);

			if (blockEntity instanceof GeoBlockEntity getBlockEntity)
				getBlockEntity.triggerAnim(this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
		});
	}
}
