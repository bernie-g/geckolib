package software.bernie.geckolib.event.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for items being rendered by {@link GeoItemRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Item} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Item animatable class type
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public class CompileItemRenderStateEvent<T extends Item & GeoAnimatable> implements GeoRenderEvent.Item.CompileRenderState<T> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoItemRenderer<T> renderer;
    private final GeoRenderState renderState;
    private final T animatable;
    private final GeoItemRenderer.RenderData renderData;

    public CompileItemRenderStateEvent(GeoItemRenderer<T> renderer, GeoRenderState renderState, T animatable, GeoItemRenderer.RenderData renderData) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.animatable = animatable;
        this.renderData = renderData;
    }

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }

    @Override
    public T getAnimatable() {
        return this.animatable;
    }

    @Override
    public GeoItemRenderer.RenderData getRenderData() {
        return this.renderData;
    }

    @ApiStatus.Internal
    @Override
    public GeoRenderState getRenderState() {
        return this.renderState;
    }

    /**
     * Event listener interface for the {@link Item.CompileRenderState} GeoRenderEvent
     *
     * @param <T> Item animatable class type
     */
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.item.Item & GeoAnimatable> {
        void handle(CompileItemRenderStateEvent<T> event);
    }
}
