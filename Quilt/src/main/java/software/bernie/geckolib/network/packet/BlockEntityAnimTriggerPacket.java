package software.bernie.geckolib.network.packet;

import javax.annotation.Nullable;

import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PacketSender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.network.AbstractPacket;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.util.ClientUtils;

/**
 * Packet for syncing user-definable animations that can be triggered from the
 * server for {@link net.minecraft.world.level.block.entity.BlockEntity
 * BlockEntities}
 */
public class BlockEntityAnimTriggerPacket extends AbstractPacket {
    private final BlockPos BLOCK_POS;
    private final String CONTROLLER_NAME;
    private final String ANIM_NAME;

    public BlockEntityAnimTriggerPacket(BlockPos blockPos, @Nullable String controllerName, String animName) {
        this.BLOCK_POS = blockPos;
        this.CONTROLLER_NAME = controllerName == null ? "" : controllerName;
        this.ANIM_NAME = animName;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(this.BLOCK_POS);
        buf.writeUtf(this.CONTROLLER_NAME);
        buf.writeUtf(this.ANIM_NAME);

        return buf;
    }

    @Override
    public ResourceLocation getPacketID() {
        return GeckoLibNetwork.BLOCK_ENTITY_ANIM_TRIGGER_SYNC_PACKET_ID;
    }

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final BlockPos BLOCK_POS = buf.readBlockPos();
        final String CONTROLLER_NAME = buf.readUtf();
        final String ANIM_NAME = buf.readUtf();

        client.execute(() -> runOnThread(BLOCK_POS, CONTROLLER_NAME, ANIM_NAME));
    }

    private static void runOnThread(BlockPos blockPos, String controllerName, String animName) {
        BlockEntity blockEntity = ClientUtils.getLevel().getBlockEntity(blockPos);

        if (blockEntity instanceof GeoBlockEntity getBlockEntity)
            getBlockEntity.triggerAnim(controllerName.isEmpty() ? null : controllerName, animName);
    }
}
