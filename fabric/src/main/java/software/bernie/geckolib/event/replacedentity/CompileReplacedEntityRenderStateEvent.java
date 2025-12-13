package software.bernie.geckolib.event.replacedentity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link ReplacedEntity} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Entity animatable class type. This is the animatable being rendered
 * @param <E> Entity class type. This is the entity being replaced
 * @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public class CompileReplacedEntityRenderStateEvent<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> implements GeoRenderEvent.ReplacedEntity.CompileRenderState<T, E, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoReplacedEntityRenderer<T, E, R> renderer;
    private final R renderState;
    private final T animatable;
    private final E entity;

    public CompileReplacedEntityRenderStateEvent(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.animatable = animatable;
        this.entity = entity;
    }

    @Override
    public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }

    @Override
    public net.minecraft.world.entity.Entity getReplacedEntity() {
        return this.entity;
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
     * Event listener interface for the {@link ReplacedEntity.CompileRenderState} GeoRenderEvent
     *
     * @param <T> Entity animatable class type. This is the animatable being rendered
     * @param <E> Entity class type. This is the entity being replaced
     * @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
     */
    @FunctionalInterface
    public interface Listener<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> {
        void handle(CompileReplacedEntityRenderStateEvent<T, E, R> event);
    }
}
