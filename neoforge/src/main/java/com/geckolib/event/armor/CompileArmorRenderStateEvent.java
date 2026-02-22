package com.geckolib.event.armor;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
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
 * @see GeoRenderEvent
 * @see CompileRenderState
 */
public class CompileArmorRenderStateEvent<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Event implements GeoRenderEvent.Armor.CompileRenderState<T, R> {
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
}
