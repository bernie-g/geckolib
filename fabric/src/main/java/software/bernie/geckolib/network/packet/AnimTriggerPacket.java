package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;

import javax.annotation.Nullable;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server
 */
public class AnimTriggerPacket extends AbstractPacket {
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

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeUtf(this.syncableId);
        buf.writeVarLong(this.instanceId);
        buf.writeUtf(this.controllerName);
        buf.writeUtf(this.animName);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.ANIM_TRIGGER_SYNC_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        String syncableId = buf.readUtf();
        long instanceId = buf.readVarLong();
        String controllerName = buf.readUtf();
        String animName = buf.readUtf();

        client.execute(() -> runOnThread(syncableId, instanceId, controllerName, animName));
    }

    private static <D> void runOnThread(String syncableId, long instanceId, String controllerName, String animName) {
        GeoAnimatable animatable = GeckoLibNetwork.getSyncedAnimatable(syncableId);

        if (animatable != null) {
            AnimatableManager<?> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);

            manager.tryTriggerAnimation(controllerName, animName);
        }
    }
}
