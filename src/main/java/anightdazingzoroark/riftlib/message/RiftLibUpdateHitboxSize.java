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

public class RiftLibUpdateHitboxSize implements IMessage {
    private int entityId;
    private String hitboxName;
    private float width, height;

    public RiftLibUpdateHitboxSize() {}

    public RiftLibUpdateHitboxSize(Entity entity, String hitboxName, float width, float height) {
        this.entityId = entity.getEntityId();
        this.hitboxName = hitboxName;
        this.width = width;
        this.height = height;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();

        int stringLength = buf.readInt();
        byte[] stringBytes = new byte[stringLength];
        buf.readBytes(stringBytes);
        this.hitboxName = new String(stringBytes);

        this.width = buf.readFloat();
        this.height = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);

        byte[] stringBytes = this.hitboxName.getBytes();
        buf.writeInt(stringBytes.length);
        buf.writeBytes(stringBytes);

        buf.writeFloat(this.width);
        buf.writeFloat(this.height);
    }

    public static class Handler implements IMessageHandler<RiftLibUpdateHitboxSize, IMessage> {
        @Override
        public IMessage onMessage(RiftLibUpdateHitboxSize message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(RiftLibUpdateHitboxSize message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayer messagePlayer = ctx.getServerHandler().player;
                Entity entity = messagePlayer.world.getEntityByID(message.entityId);

                if (entity instanceof IMultiHitboxUser) {
                    IMultiHitboxUser hitboxUser = (IMultiHitboxUser) entity;
                    hitboxUser.updateHitboxScaleFromAnim(message.hitboxName, message.width, message.height);
                }
            }
            if (ctx.side == Side.CLIENT) {
                EntityPlayer messagePlayer = Minecraft.getMinecraft().player;
                Entity entity = messagePlayer.world.getEntityByID(message.entityId);

                if (entity instanceof IMultiHitboxUser) {
                    IMultiHitboxUser hitboxUser = (IMultiHitboxUser) entity;
                    hitboxUser.updateHitboxScaleFromAnim(message.hitboxName, message.width, message.height);
                }
            }
        }
    }
}
