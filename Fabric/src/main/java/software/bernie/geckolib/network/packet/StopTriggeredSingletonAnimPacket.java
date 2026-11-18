package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;

import javax.annotation.Nullable;

public class StopTriggeredSingletonAnimPacket extends AbstractPacket {
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
        return GeckoLibNetwork.STOP_TRIGGERED_ANIM_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        String syncableId = buf.readUtf();
        long instanceId = buf.readVarLong();
        String controllerName = buf.readUtf();
        String animName = buf.readUtf();

        client.execute(() -> runOnThread(syncableId, instanceId, controllerName, animName));
    }

    private static <D> void runOnThread(String syncableId, long instanceId, String controllerName, String animName) {
        if (GeckoLibNetwork.getSyncedAnimatable(syncableId) instanceof SingletonGeoAnimatable animatable) {
            AnimatableManager<GeoAnimatable> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);

            if (animatableManager == null)
                return;

            if (!controllerName.isEmpty()) {
                animatableManager.stopTriggeredAnimation(controllerName, animName.isEmpty() ? null : animName);
            }
            else {
                animatableManager.stopTriggeredAnimation(animName.isEmpty() ? null : animName);
            }
        }
    }
}
