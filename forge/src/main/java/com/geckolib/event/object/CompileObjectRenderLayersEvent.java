package com.geckolib.event.object;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
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
public record CompileObjectRenderLayersEvent<T extends GeoAnimatable, E, R extends GeoRenderState>
        (GeoObjectRenderer<T, E, R> renderer)
        implements GeoRenderEvent.Object.CompileRenderLayers<T, E, R>, RecordEvent {
    public static final EventBus<CompileObjectRenderLayersEvent> BUS = EventBus.create(CompileObjectRenderLayersEvent.class);

    @Override
    public GeoObjectRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }
}
