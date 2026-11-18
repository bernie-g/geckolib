package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.GeckoLibNetwork;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Packet for syncing user-definable animations that can be triggered from the server
 */
public class StopTriggeredSingletonAnimPacket {
	private final String syncableId;
	private final long instanceId;
	private final String controllerName;
	private final String animName;

	public StopTriggeredSingletonAnimPacket(String syncableId, long instanceId, @Nullable String controllerName, @Nullable String animName) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.controllerName = controllerName == null ? "" : controllerName;
		this.animName = animName == null ? "" : animName;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(this.syncableId);
		buffer.writeVarLong(this.instanceId);
		buffer.writeUtf(this.controllerName);
		buffer.writeUtf(this.animName);
	}

	public static StopTriggeredSingletonAnimPacket decode(FriendlyByteBuf buffer) {
		return new StopTriggeredSingletonAnimPacket(buffer.readUtf(), buffer.readVarLong(), buffer.readUtf(), buffer.readUtf());
	}

	public void receivePacket(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context handler = context.get();

		handler.enqueueWork(() -> {
			if (GeckoLibNetwork.getSyncedAnimatable(this.syncableId) instanceof SingletonGeoAnimatable animatable) {
				AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);

				if (animatableManager == null)
					return;

				if (!this.controllerName.isEmpty()) {
					animatableManager.stopTriggeredAnimation(this.controllerName, this.animName.isEmpty() ? null : this.animName);
				}
				else {
					animatableManager.stopTriggeredAnimation(this.animName.isEmpty() ? null : this.animName);
				}
			}
		});
		handler.setPacketHandled(true);
	}
}
