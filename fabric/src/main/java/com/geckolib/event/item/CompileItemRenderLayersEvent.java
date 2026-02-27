package com.geckolib.event.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoItemRenderer;

/// One-time event for a [GeoItemRenderer] called on first initialisation
///
/// Use this event to add render layers to the renderer as needed
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @param <T> Item animatable class type
/// @see GeoRenderEvent
/// @see CompileRenderLayers
public class CompileItemRenderLayersEvent<T extends Item & GeoAnimatable> implements GeoRenderEvent.Item.CompileRenderLayers<T> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoItemRenderer<T> renderer;

    public CompileItemRenderLayersEvent(GeoItemRenderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return this.renderer;
    }

    /// Event listener interface for the [Item.CompileRenderLayers] GeoRenderEvent
    ///
    /// @param <T> Item animatable class type
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.item.Item & GeoAnimatable> {
        void handle(CompileItemRenderLayersEvent<T> event);
    }
}
