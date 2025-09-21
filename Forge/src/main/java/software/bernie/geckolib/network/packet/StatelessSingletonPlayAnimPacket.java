package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.stateless.StatelessGeoSingletonAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.NetworkUtil;

import java.util.function.Supplier;

/**
 * Packet for instructing clients to set a play state for an animation for a {@link StatelessGeoSingletonAnimatable}
 */
public class StatelessSingletonPlayAnimPacket {
	private final String syncableId;
	private final long instanceId;
	private final RawAnimation animation;

	public StatelessSingletonPlayAnimPacket(String syncableId, long instanceId, RawAnimation animation) {
		this.syncableId = syncableId;
		this.instanceId = instanceId;
		this.animation = animation;
	}

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.syncableId);
        buffer.writeVarLong(this.instanceId);
        NetworkUtil.writeRawAnimationToBuffer(animation, buffer);
    }

    public static StatelessSingletonPlayAnimPacket decode(FriendlyByteBuf buffer) {
        final String syncableId = buffer.readUtf();
        final long instanceID = buffer.readVarLong();
        final RawAnimation animation = NetworkUtil.readRawAnimationFromBuffer(buffer);

        return new StatelessSingletonPlayAnimPacket(syncableId, instanceID, animation);
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();

        handler.enqueueWork(() -> {
            GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

            if (animatable instanceof StatelessGeoSingletonAnimatable statelessAnimatable)
                statelessAnimatable.handleClientAnimationPlay(animatable, instanceId, animation);
        });
    }
}
