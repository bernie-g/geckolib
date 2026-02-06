package software.bernie.geckolib.mixin.client;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

/// Duck-typing mixin to apply the [GeoRenderState] duck interface to `BlockEntityRenderStates`
@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(BlockEntityRenderState.class)
public class BlockEntityRenderStateMixin implements GeoRenderState {
    @Shadow
    public int lightCoords;
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

    @ApiStatus.Internal
    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        return this.geckolib$data;
    }
}
