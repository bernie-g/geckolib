package software.bernie.geckolib.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Entity} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public class CompileEntityRenderStateEvent<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> implements GeoRenderEvent.Entity.CompileRenderState<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoEntityRenderer<T, R> renderer;
    private final R renderState;
    private final T animatable;

    public CompileEntityRenderStateEvent(GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.animatable = animatable;
    }

    @Override
    public GeoEntityRenderer<T, R> getRenderer() {
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

    /**
     * Event listener interface for the {@link Entity.CompileRenderState} GeoRenderEvent
     */
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> {
        void handle(CompileEntityRenderStateEvent<T, R> event);
    }
}
