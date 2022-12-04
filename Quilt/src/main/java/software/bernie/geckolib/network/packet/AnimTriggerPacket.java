package software.bernie.geckolib.network.packet;

import javax.annotation.Nullable;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server
 */
public class AnimTriggerPacket extends AbstractPacket {

    private final String SYNCABLE_ID;
    private final long INSTANCE_ID;
    private final String CONTROLLER_NAME;
    private final String ANIM_NAME;

    public AnimTriggerPacket(String syncableId, long instanceId, @Nullable String controllerName, String animName) {
        this.SYNCABLE_ID = syncableId;
        this.INSTANCE_ID = instanceId;
        this.CONTROLLER_NAME = controllerName == null ? "" : controllerName;
        this.ANIM_NAME = animName;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(this.SYNCABLE_ID);

        buf.writeVarLong(this.INSTANCE_ID);
        buf.writeUtf(this.CONTROLLER_NAME);

        buf.writeUtf(this.ANIM_NAME);
        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.ANIM_TRIGGER_SYNC_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final String SYNCABLE_ID = buf.readUtf();
        final long INSTANCE_ID = buf.readVarLong();

        final String CONTROLLER_NAME = buf.readUtf();
        final String ANIM_NAME = buf.readUtf();

        client.execute(() -> runOnThread(SYNCABLE_ID, INSTANCE_ID, CONTROLLER_NAME, ANIM_NAME));
    }

    private static <D> void runOnThread(String syncableId, long instanceId, String controllerName, String animName) {
        GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

        if (animatable != null) {
            AnimatableManager<?> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);

            manager.tryTriggerAnimation(controllerName, animName);
        }
    }
}
