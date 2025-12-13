package software.bernie.geckolib.event.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * One-time event for a {@link GeoItemRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Item animatable class type
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public class CompileItemRenderLayersEvent<T extends Item & GeoAnimatable> implements GeoRenderEvent.Item.CompileRenderLayers<T> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoItemRenderer<T> renderer;

    public CompileItemRenderLayersEvent(GeoItemRenderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }

    /**
     * Event listener interface for the {@link Item.CompileRenderLayers} GeoRenderEvent
     *
     * @param <T> Item animatable class type
     */
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.item.Item & GeoAnimatable> {
        void handle(CompileItemRenderLayersEvent<T> event);
    }
}
