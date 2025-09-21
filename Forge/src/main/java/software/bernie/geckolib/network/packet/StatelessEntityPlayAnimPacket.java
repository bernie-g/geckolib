package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.stateless.StatelessAnimatable;
import software.bernie.geckolib.animatable.stateless.StatelessGeoEntity;
import software.bernie.geckolib.animatable.stateless.StatelessGeoReplacedEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.NetworkUtil;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Supplier;

/**
 * Packet for instructing clients to set a play state for an animation for a {@link StatelessGeoEntity} or
 * {@link StatelessGeoReplacedEntity}
 */
public class StatelessEntityPlayAnimPacket {
    private final int entityId;
    private final boolean isReplacedEntity;

    private final RawAnimation animation;

    public StatelessEntityPlayAnimPacket(int entityId, RawAnimation animation) {
        this(entityId, false, animation);
    }

    public StatelessEntityPlayAnimPacket(int entityId, boolean isReplacedEntity, RawAnimation animation) {
        this.entityId = entityId;
        this.isReplacedEntity = isReplacedEntity;
        this.animation = animation;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
        buffer.writeBoolean(this.isReplacedEntity);

        NetworkUtil.writeRawAnimationToBuffer(this.animation, buffer);
    }

    public static StatelessEntityPlayAnimPacket decode(FriendlyByteBuf buffer) {
        final int entityId = buffer.readVarInt();
        final boolean isReplacedEntity = buffer.readBoolean();
        final RawAnimation animation = NetworkUtil.readRawAnimationFromBuffer(buffer);

        return new StatelessEntityPlayAnimPacket(entityId, isReplacedEntity, animation);
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context handler = context.get();

        handler.enqueueWork(() -> {
            Entity entity = ClientUtils.getLevel().getEntity(entityId);

            if (entity == null)
                return;

            GeoAnimatable animatable = isReplacedEntity ?
                                       RenderUtils.getReplacedAnimatable(entity.getType()) :
                                       entity instanceof GeoAnimatable entityAnimatable ? entityAnimatable : null;

            if (animatable instanceof StatelessAnimatable statelessAnimatable)
                statelessAnimatable.handleClientAnimationPlay(animatable, entityId, animation);
        });
    }
}
