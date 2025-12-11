package software.bernie.geckolib.event.object;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for objects being rendered by {@link GeoObjectRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Object} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public record CompileObjectRenderStateEvent<T extends GeoAnimatable, E, R extends GeoRenderState>
        (GeoObjectRenderer<T, E, R> renderer, R renderState, T animatable, @Nullable E relatedObject)
        implements GeoRenderEvent.Object.CompileRenderState<T, E, R>, RecordEvent {
    public static final EventBus<CompileObjectRenderStateEvent> BUS = EventBus.create(CompileObjectRenderStateEvent.class);

    @Override
    public GeoObjectRenderer<T, E, R> getRenderer() {
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

    @Override
    public @Nullable E getRelatedObject() {
        return this.relatedObject;
    }
}
