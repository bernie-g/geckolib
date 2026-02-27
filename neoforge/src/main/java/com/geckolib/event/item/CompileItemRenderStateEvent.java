package com.geckolib.event.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/// Pre-render event for items being rendered by [GeoItemRenderer]
///
/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
///
/// Use this event to add data that you may need in a later [Item] event, or to override/replace data used in rendering
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @see GeoRenderEvent
/// @see CompileRenderState
public class CompileItemRenderStateEvent<T extends Item & GeoAnimatable> extends Event implements GeoRenderEvent.Item.CompileRenderState<T> {
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
}
