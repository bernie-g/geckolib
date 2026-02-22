package com.geckolib.event.replacedentity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoReplacedEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/**
 * One-time event for a {@link GeoReplacedEntityRenderer} called on first initialisation
 * <p>
 * Use this event to add render layers to the renderer as needed
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see CompileRenderLayers
 */
public class CompileReplacedEntityRenderLayersEvent<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> extends Event implements GeoRenderEvent.ReplacedEntity.CompileRenderLayers<T, E, R> {
    private final GeoReplacedEntityRenderer<T, E, R> renderer;

    public CompileReplacedEntityRenderLayersEvent(GeoReplacedEntityRenderer<T, E, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }
}
