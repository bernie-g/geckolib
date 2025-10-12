package software.bernie.geckolib.event.object;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoObjectRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public class CompileObjectRenderLayersEvent<T extends GeoAnimatable, E, R extends GeoRenderState> implements GeoRenderEvent.Object.CompileRenderLayers<T, E, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoObjectRenderer<T, E, R> renderer;

    public CompileObjectRenderLayersEvent(GeoObjectRenderer<T, E, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoObjectRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }

    /**
     * Event listener interface for the {@link Object.CompileRenderLayers} GeoRenderEvent
     */
    @FunctionalInterface
    public interface Listener<T extends GeoAnimatable, E, R extends GeoRenderState> {
        void handle(CompileObjectRenderLayersEvent<T, E, R> event);
    }
}
