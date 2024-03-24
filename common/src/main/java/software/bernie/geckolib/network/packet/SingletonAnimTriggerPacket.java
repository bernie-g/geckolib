package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public record SingletonAnimTriggerPacket(String syncableId, long instanceId, String controllerName, String animName) implements MultiloaderPacket {
    public static final ResourceLocation ID = GeckoLibConstants.id("singleton_anim_trigger");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.syncableId);
        buffer.writeVarLong(this.instanceId);
        buffer.writeUtf(this.controllerName);
        buffer.writeUtf(this.animName);
    }

    public static SingletonAnimTriggerPacket decode(FriendlyByteBuf buffer) {
        return new SingletonAnimTriggerPacket(buffer.readUtf(), buffer.readVarLong(), buffer.readUtf(), buffer.readUtf());
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            GeoAnimatable animatable = GeckoLibUtil.getSyncedAnimatable(this.syncableId);

            if (animatable != null)
                animatable.getAnimatableInstanceCache().getManagerForId(this.instanceId).tryTriggerAnimation(this.controllerName, this.animName);
        });
    }
}
