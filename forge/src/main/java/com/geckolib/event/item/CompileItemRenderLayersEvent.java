package com.geckolib.event.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
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
public record CompileItemRenderLayersEvent<T extends Item & GeoAnimatable>
        (GeoItemRenderer<T> renderer)
        implements GeoRenderEvent.Item.CompileRenderLayers<T>, RecordEvent {
    public static final EventBus<CompileItemRenderLayersEvent> BUS = EventBus.create(CompileItemRenderLayersEvent.class);

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }
}
