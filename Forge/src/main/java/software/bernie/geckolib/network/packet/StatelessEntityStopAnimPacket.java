package software.bernie.geckolib.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.stateless.StatelessAnimatable;
import software.bernie.geckolib.animatable.stateless.StatelessGeoEntity;
import software.bernie.geckolib.animatable.stateless.StatelessGeoReplacedEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Supplier;

/**
 * Packet for instructing clients to set a stop state for an animation for a {@link StatelessGeoEntity} or
 * {@link StatelessGeoReplacedEntity}
 */
public class StatelessEntityStopAnimPacket {
    private final int entityId;
    private final boolean isReplacedEntity;

    private final String animation;

    public StatelessEntityStopAnimPacket(int entityId, String animation) {
        this(entityId, false, animation);
    }

    public StatelessEntityStopAnimPacket(int entityId, boolean isReplacedEntity, String animation) {
        this.entityId = entityId;
        this.isReplacedEntity = isReplacedEntity;
        this.animation = animation;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
        buffer.writeBoolean(this.isReplacedEntity);
        buffer.writeUtf(this.animation);
    }

    public static StatelessEntityStopAnimPacket decode(FriendlyByteBuf buffer) {
        final int entityId = buffer.readVarInt();
        final boolean isReplacedEntity = buffer.readBoolean();
        final String animation = buffer.readUtf();

        return new StatelessEntityStopAnimPacket(entityId, isReplacedEntity, animation);
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
                statelessAnimatable.handleClientAnimationStop(animatable, entityId, animation);
        });
    }
}
