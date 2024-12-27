package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Packet for syncing user-definable animations that can be triggered from the server for {@link Entity Entities}
 */
public class StopTriggeredEntityAnimPacket {
	private final int entityId;
	private final boolean isReplacedEntity;
	private final String controllerName;
	private final String animName;

	public StopTriggeredEntityAnimPacket(int entityId, @Nullable String controllerName, @Nullable String animName) {
		this(entityId, false, controllerName, animName);
	}

	public StopTriggeredEntityAnimPacket(int entityId, boolean isReplacedEntity, @Nullable String controllerName, @Nullable String animName) {
		this.entityId = entityId;
		this.isReplacedEntity = isReplacedEntity;
		this.controllerName = controllerName == null ? "" : controllerName;
		this.animName = animName == null ? "" : animName;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(this.entityId);
		buffer.writeBoolean(this.isReplacedEntity);
		buffer.writeUtf(this.controllerName);
		buffer.writeUtf(this.animName);
	}

	public static StopTriggeredEntityAnimPacket decode(FriendlyByteBuf buffer) {
		return new StopTriggeredEntityAnimPacket(buffer.readVarInt(), buffer.readBoolean(), buffer.readUtf(), buffer.readUtf());
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
					replacedEntity.stopTriggeredAnimation(entity, this.controllerName.isEmpty() ? null : this.controllerName, this.animName.isEmpty() ? null : this.animName);
			}
			else if (entity instanceof GeoEntity geoEntity) {
				geoEntity.stopTriggeredAnimation(this.controllerName.isEmpty() ? null : this.controllerName, this.animName.isEmpty() ? null : this.animName);
			}
		});
		handler.setPacketHandled(true);
	}
}
