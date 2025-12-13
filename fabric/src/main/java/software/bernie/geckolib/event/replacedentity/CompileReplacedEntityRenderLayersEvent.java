package software.bernie.geckolib.event.replacedentity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoReplacedEntityRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Entity animatable class type. This is the animatable being rendered
 * @param <E> Entity class type. This is the entity being replaced
 * @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public class CompileReplacedEntityRenderLayersEvent<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> implements GeoRenderEvent.ReplacedEntity.CompileRenderLayers<T, E, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoReplacedEntityRenderer<T, E, R> renderer;

    public CompileReplacedEntityRenderLayersEvent(GeoReplacedEntityRenderer<T, E, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }

    /**
     * Event listener interface for the {@link ReplacedEntity.CompileRenderLayers} GeoRenderEvent
     *
     * @param <T> Entity animatable class type. This is the animatable being rendered
     * @param <E> Entity class type. This is the entity being replaced
     * @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
     */
    @FunctionalInterface
    public interface Listener<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> {
        void handle(CompileReplacedEntityRenderLayersEvent<T, E, R> event);
    }
}
