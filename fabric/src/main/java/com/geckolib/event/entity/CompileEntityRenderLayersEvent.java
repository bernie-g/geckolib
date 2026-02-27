package com.geckolib.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

/// One-time event for a [GeoEntityRenderer] called on first initialisation
///
/// Use this event to add render layers to the renderer as needed
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @param <T> Entity animatable class type
/// @param <R> RenderState class type
/// @see GeoRenderEvent
/// @see CompileRenderLayers
public class CompileEntityRenderLayersEvent<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> implements GeoRenderEvent.Entity.CompileRenderLayers<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoEntityRenderer<T, R> renderer;

    public CompileEntityRenderLayersEvent(GeoEntityRenderer<T, R> renderer) {
        this.renderer = renderer;
    }

    @Override
    public GeoEntityRenderer<T, R> getRenderer() {
        return this.renderer;
    }

    /// Event listener interface for the [Entity.CompileRenderLayers] GeoRenderEvent
    ///
    /// @param <T> Entity animatable class type
    /// @param <R> RenderState class type
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> {
        void handle(CompileEntityRenderLayersEvent<T, R> event);
    }
}
