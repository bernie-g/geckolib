package software.bernie.geckolib.event.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for blocks being rendered by {@link GeoBlockRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Block} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public record CompileBlockRenderStateEvent<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState>
        (GeoBlockRenderer<T, R> renderer, R renderState, T animatable)
        implements GeoRenderEvent.Block.CompileRenderState<T, R>, RecordEvent {
    public static final EventBus<CompileBlockRenderStateEvent> BUS = EventBus.create(CompileBlockRenderStateEvent.class);

    @Override
    public GeoBlockRenderer<T, R> getRenderer() {
        return this.renderer;
    }

    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    @ApiStatus.Internal
    @Override
    public R getRenderState() {
        return this.renderState;
    }
}
