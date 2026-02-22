package com.geckolib.event.object;

import net.neoforged.bus.api.Event;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;

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
public class CompileObjectRenderLayersEvent<T extends GeoAnimatable, E, R extends GeoRenderState> extends Event implements GeoRenderEvent.Object.CompileRenderLayers<T, E, R> {
    private final GeoObjectRenderer<T, E, R> renderer;

    public CompileObjectRenderLayersEvent(GeoObjectRenderer<T, E, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoObjectRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }
}
