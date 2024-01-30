package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.TransitionRenderUtils;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;

/**
 * Packet for syncing user-definable animations that can be triggered from the server for {@link net.minecraft.world.entity.Entity Entities}
 */
public record EntityAnimTriggerPacket<D>(int entityId, boolean isReplacedEntity, @Nullable String controllerName, String animName) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(GeckoLib.MOD_ID, "entity_anim_trigger");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public EntityAnimTriggerPacket(int entityId, @Nullable String controllerName, String animName) {
		this(entityId, false, controllerName, animName);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.entityId);
		buffer.writeBoolean(this.isReplacedEntity);
		buffer.writeUtf(this.controllerName == null ? "" : this.controllerName);
		buffer.writeUtf(this.animName);
	}

	public static <D> EntityAnimTriggerPacket<D> decode(FriendlyByteBuf buffer) {
		return new EntityAnimTriggerPacket<>(buffer.readVarInt(), buffer.readBoolean(), buffer.readUtf(), buffer.readUtf());
	}

	public void receivePacket(PlayPayloadContext context) {
		context.workHandler().execute(() -> {
			Entity entity = ClientUtils.getLevel().getEntity(this.entityId);

			if (entity == null)
				return;

			if (this.isReplacedEntity) {
				GeoAnimatable animatable = TransitionRenderUtils.getReplacedAnimatable(entity.getType());

				if (animatable instanceof GeoReplacedEntity replacedEntity)
					replacedEntity.triggerAnim(entity, this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
			}
			else if (entity instanceof GeoEntity geoEntity) {
				geoEntity.triggerAnim(this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
			}
		});
	}
}
