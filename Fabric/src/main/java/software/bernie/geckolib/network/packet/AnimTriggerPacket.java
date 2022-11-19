package software.bernie.geckolib.network.packet;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.GeckoLibNetwork;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server
 */
public class AnimTriggerPacket<D> {
	private final String syncableId;
	private final long instanceId;
	private final String controllerName;
	private final String animName;

	public AnimTriggerPacket(String syncableId, long instanceId, @Nullable String controllerName, String animName) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.controllerName = controllerName == null ? "" : controllerName;
		this.animName = animName;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.syncableId);
		buffer.writeVarLong(this.instanceId);
		buffer.writeUtf(this.controllerName);
		buffer.writeUtf(this.animName);
	}

	public static <D> AnimTriggerPacket<D> decode(FriendlyByteBuf buffer) {
		return new AnimTriggerPacket<>(buffer.readUtf(), buffer.readVarLong(), buffer.readUtf(), buffer.readUtf());
	}

	public void receivePacket() {
		GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(this.syncableId);

		if (animatable != null) {
			AnimatableManager<?> manager = animatable.getFactory().getManagerForId(this.instanceId);

			manager.tryTriggerAnimation(this.controllerName, this.animName);
		}
	}
}
