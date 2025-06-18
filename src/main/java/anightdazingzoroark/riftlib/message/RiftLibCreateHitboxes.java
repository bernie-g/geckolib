package anightdazingzoroark.riftlib.message;

import anightdazingzoroark.riftlib.file.HitboxDefinitionList;
import anightdazingzoroark.riftlib.hitboxLogic.EntityHitbox;
import anightdazingzoroark.riftlib.hitboxLogic.IMultiHitboxUser;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class RiftLibCreateHitboxes implements IMessage {
    private int entityId;
    private int hitboxDefinitionListSize;
    private HitboxDefinitionList hitboxDefinitionList;

    public RiftLibCreateHitboxes() {}

    public RiftLibCreateHitboxes(EntityLiving entityLiving, HitboxDefinitionList hitboxDefinitionList) {
        this.entityId = entityLiving.getEntityId();
        this.hitboxDefinitionListSize = hitboxDefinitionList.list.size();
        this.hitboxDefinitionList = hitboxDefinitionList;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.hitboxDefinitionListSize = buf.readInt();
        HitboxDefinitionList list = new HitboxDefinitionList();
        for (int i = 0; i < this.hitboxDefinitionListSize; i++) {
            HitboxDefinitionList.HitboxDefinition hitboxDefinition = this.readHitboxDefinitionFromBuf(buf);
            list.list.add(hitboxDefinition);
        }
        this.hitboxDefinitionList = list;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.hitboxDefinitionListSize);
        for (HitboxDefinitionList.HitboxDefinition hitboxDefinition : this.hitboxDefinitionList.list) {
            this.writeHitboxDefinitionToBuf(buf, hitboxDefinition);
        }
    }

    public static class Handler implements IMessageHandler<RiftLibCreateHitboxes, IMessage> {
        @Override
        public IMessage onMessage(RiftLibCreateHitboxes message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(RiftLibCreateHitboxes message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP playerEntity = ctx.getServerHandler().player;
                EntityLiving entityLiving = (EntityLiving) playerEntity.world.getEntityByID(message.entityId);
                if (entityLiving != null) {
                    List<Entity> hitboxesToCreate = new ArrayList<>();

                    for (HitboxDefinitionList.HitboxDefinition hitboxDefinition : message.hitboxDefinitionList.list) {
                        EntityHitbox entityHitbox = new EntityHitbox(
                                (IMultiHitboxUser) entityLiving,
                                hitboxDefinition.locator,
                                1f,
                                hitboxDefinition.width,
                                hitboxDefinition.height,
                                (float) hitboxDefinition.position.x,
                                (float) hitboxDefinition.position.y,
                                (float) hitboxDefinition.position.z
                        );
                        hitboxesToCreate.add(entityHitbox);
                    }

                    ((IMultiHitboxUser) entityLiving).setParts(this.listToArray(hitboxesToCreate));
                }
            }
            if (ctx.side == Side.CLIENT) {
                EntityPlayer playerEntity = Minecraft.getMinecraft().player;
                EntityLiving entityLiving = (EntityLiving) playerEntity.world.getEntityByID(message.entityId);
                if (entityLiving != null) {
                    List<Entity> hitboxesToCreate = new ArrayList<>();

                    for (HitboxDefinitionList.HitboxDefinition hitboxDefinition : message.hitboxDefinitionList.list) {
                        EntityHitbox entityHitbox = new EntityHitbox(
                                (IMultiHitboxUser) entityLiving,
                                hitboxDefinition.locator,
                                1f,
                                hitboxDefinition.width,
                                hitboxDefinition.height,
                                (float) hitboxDefinition.position.x,
                                (float) hitboxDefinition.position.y,
                                (float) hitboxDefinition.position.z
                        );
                        hitboxesToCreate.add(entityHitbox);
                    }

                    ((IMultiHitboxUser) entityLiving).setParts(this.listToArray(hitboxesToCreate));
                }
            }
        }

        private Entity[] listToArray(List<Entity> list) {
            Entity[] arrayToReturn = new Entity[list.size()];
            for (int x = 0; x < list.size(); x++) {
                arrayToReturn[x] = list.get(x);
            }
            return arrayToReturn;
        }
    }

    private void writeHitboxDefinitionToBuf(ByteBuf buf, HitboxDefinitionList.HitboxDefinition hitboxDefinition) {
        ByteBufUtils.writeUTF8String(buf, hitboxDefinition.locator);
        buf.writeFloat(hitboxDefinition.width);
        buf.writeFloat(hitboxDefinition.height);
        buf.writeBoolean(hitboxDefinition.affectedByAnim);
        buf.writeDouble(hitboxDefinition.position.x);
        buf.writeDouble(hitboxDefinition.position.y);
        buf.writeDouble(hitboxDefinition.position.z);
    }

    private HitboxDefinitionList.HitboxDefinition readHitboxDefinitionFromBuf(ByteBuf buf) {
        String locator = ByteBufUtils.readUTF8String(buf);
        float width = buf.readFloat();
        float height = buf.readFloat();
        boolean affectedByAnim = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        HitboxDefinitionList.HitboxDefinition toReturn = new HitboxDefinitionList.HitboxDefinition(locator, width, height, affectedByAnim);
        toReturn.position = new Vec3d(x, y, z);
        return toReturn;
    }
}
