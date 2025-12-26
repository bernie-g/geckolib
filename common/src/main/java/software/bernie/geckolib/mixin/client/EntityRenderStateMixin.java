package software.bernie.geckolib.mixin.client;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

/**
 * Duck-typing mixin to apply the {@link GeoRenderState} duck interface to {@code EntityRenderStates}
 */
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements GeoRenderState {
    @Shadow
    public int lightCoords;
    @Shadow
    public float ageInTicks;
    @Unique
    private final Map<DataTicket<?>, Object> geckolib$data = new Reference2ObjectOpenHashMap<>();

    @Unique
    @Override
    public <D> void addGeckolibData(DataTicket<D> dataTicket, D data) {
        this.geckolib$data.put(dataTicket, data);
    }

    @Unique
    @Override
    public boolean hasGeckolibData(DataTicket<?> dataTicket) {
        return this.geckolib$data.containsKey(dataTicket);
    }

    @Unique
    @Override
    public int getPackedLight() {
        return getOrDefaultGeckolibData(DataTickets.PACKED_LIGHT, this.lightCoords);
    }

    @Override
    public double getAnimatableAge() {
        return getOrDefaultGeckolibData(DataTickets.TICK, (double)this.ageInTicks);
    }

    @ApiStatus.Internal
    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.geckolib$data;
    }
}
