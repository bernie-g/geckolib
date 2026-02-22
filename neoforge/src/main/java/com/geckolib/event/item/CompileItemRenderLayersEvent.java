package com.geckolib.event.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoItemRenderer;

/**
 * One-time event for a {@link GeoItemRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public class CompileItemRenderLayersEvent<T extends Item & GeoAnimatable> extends Event implements GeoRenderEvent.Item.CompileRenderLayers<T> {
    private final GeoItemRenderer<T> renderer;

    public CompileItemRenderLayersEvent(GeoItemRenderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }
}
