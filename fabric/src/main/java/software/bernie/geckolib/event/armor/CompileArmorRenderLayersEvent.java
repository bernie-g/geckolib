package software.bernie.geckolib.event.armor;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoBlockRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Item animatable class type
 * @param <R> RenderState class type. GeckoLib armor rendering requires {@link HumanoidRenderState} as the minimum class type
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public class CompileArmorRenderLayersEvent<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> implements GeoRenderEvent.Armor.CompileRenderLayers<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });

    private final GeoArmorRenderer<T, R> renderer;

    public CompileArmorRenderLayersEvent(GeoArmorRenderer<T, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
        return this.renderer;
    }

    /**
     * Event listener interface for the {@link Armor.CompileRenderLayers} GeoRenderEvent
     *
     * @param <T> Item animatable class type
     * @param <R> RenderState class type
     */
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> {
        void handle(CompileArmorRenderLayersEvent<T, R> event);
    }
}
