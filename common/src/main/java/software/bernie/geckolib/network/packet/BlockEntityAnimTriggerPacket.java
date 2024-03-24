package software.bernie.geckolib.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.util.ClientUtil;

import java.util.function.Consumer;

public record BlockEntityAnimTriggerPacket(BlockPos pos, String controllerName, String animName) implements MultiloaderPacket {
    public static final ResourceLocation ID = GeckoLibConstants.id("blockentity_anim_trigger");

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

    public static BlockEntityAnimTriggerPacket decode(FriendlyByteBuf buffer) {
        return new BlockEntityAnimTriggerPacket(buffer.readBlockPos(), buffer.readUtf(), buffer.readUtf());
    }

    @Override
    public void receiveMessage(@Nullable Player sender, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            if (ClientUtil.getLevel().getBlockEntity(this.pos) instanceof GeoBlockEntity blockEntity)
                blockEntity.triggerAnim(this.controllerName.isEmpty() ? null : this.controllerName, this.animName);
        });
    }
}
