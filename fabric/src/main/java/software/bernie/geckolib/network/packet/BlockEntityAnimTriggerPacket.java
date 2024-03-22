package software.bernie.geckolib.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtil;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server for {@link net.minecraft.world.level.block.entity.BlockEntity
 * BlockEntities}
 */
public class BlockEntityAnimTriggerPacket extends AbstractPacket {
    private final BlockPos blockPos;
    private final String controllerName;
    private final String animName;

    public BlockEntityAnimTriggerPacket(BlockPos blockPos, @Nullable String controllerName, String animName) {
        this.blockPos = blockPos;
        this.controllerName = controllerName == null ? "" : controllerName;
        this.animName = animName;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(this.blockPos);
        buf.writeUtf(this.controllerName);
        buf.writeUtf(this.animName);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final BlockPos blockPos = buf.readBlockPos();
        final String controllerName = buf.readUtf();
        final String animName = buf.readUtf();

        client.execute(() -> runOnThread(blockPos, controllerName, animName));
    }

    private static void runOnThread(BlockPos blockPos, String controllerName, String animName) {
        BlockEntity blockEntity = ClientUtil.getLevel().getBlockEntity(blockPos);

        if (blockEntity instanceof GeoBlockEntity getBlockEntity)
            getBlockEntity.triggerAnim(controllerName.isEmpty() ? null : controllerName, animName);
    }
}
