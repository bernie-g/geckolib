package anightdazingzoroark.riftlib.message;

import anightdazingzoroark.riftlib.hitboxLogic.IMultiHitboxUser;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class RiftLibUpdateHitboxPos implements IMessage {
    private int entityId;
    private String hitboxName;
    private float x, y, z;

    public RiftLibUpdateHitboxPos() {}

    public RiftLibUpdateHitboxPos(Entity entity, String hitboxName, float x, float y, float z) {
        this.entityId = entity.getEntityId();
        this.hitboxName = hitboxName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();

        int stringLength = buf.readInt();
        byte[] stringBytes = new byte[stringLength];
        buf.readBytes(stringBytes);
        this.hitboxName = new String(stringBytes);

        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);

        byte[] stringBytes = this.hitboxName.getBytes();
        buf.writeInt(stringBytes.length);
        buf.writeBytes(stringBytes);

        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
    }

    public static class Handler implements IMessageHandler<RiftLibUpdateHitboxPos, IMessage> {
        @Override
        public IMessage onMessage(RiftLibUpdateHitboxPos message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(RiftLibUpdateHitboxPos message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayer messagePlayer = ctx.getServerHandler().player;
                Entity entity = messagePlayer.world.getEntityByID(message.entityId);

                if (entity instanceof IMultiHitboxUser) {
                    IMultiHitboxUser hitboxUser = (IMultiHitboxUser) entity;
                    hitboxUser.upateHitboxPos(message.hitboxName, message.x, message.y, message.z);
                }
            }
            if (ctx.side == Side.CLIENT) {
                EntityPlayer messagePlayer = Minecraft.getMinecraft().player;
                Entity entity = messagePlayer.world.getEntityByID(message.entityId);

                if (entity instanceof IMultiHitboxUser) {
                    IMultiHitboxUser hitboxUser = (IMultiHitboxUser) entity;
                    hitboxUser.upateHitboxPos(message.hitboxName, message.x, message.y, message.z);
                }
            }
        }
    }
}
