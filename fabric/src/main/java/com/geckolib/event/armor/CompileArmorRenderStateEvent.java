package com.geckolib.event.armor;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoItem;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/**
 * Pre-render event for armor pieces being rendered by {@link GeoArmorRenderer}
 * <p>
 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
 * <p>
 * Use this event to add data that you may need in a later {@link Armor} event, or to override/replace data used in rendering
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Item animatable class type
 * @param <R> RenderState class type - GeckoLib armor is based on Humanoid rendering and requires {@link HumanoidRenderState} as a minimum
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public class CompileArmorRenderStateEvent<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> implements GeoRenderEvent.Armor.CompileRenderState<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoArmorRenderer<T, R> renderer;
    private final R renderState;
    private final T animatable;
    private final GeoArmorRenderer.RenderData renderData;

    public CompileArmorRenderStateEvent(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, GeoArmorRenderer.RenderData renderData) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.animatable = animatable;
        this.renderData = renderData;
    }

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
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
    public GeoArmorRenderer.RenderData getRenderData() {
        return this.renderData;
    }

    /**
     * Event listener interface for the {@link Armor.CompileRenderState} GeoRenderEvent
     *
     * @param <T> Item animatable class type
     * @param <R> RenderState class type - GeckoLib armor is based on Humanoid rendering and requires {@link HumanoidRenderState} as a minimum
     */
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> {
        void handle(CompileArmorRenderStateEvent<T, R> event);
    }
}
