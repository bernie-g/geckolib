package com.geckolib.event.object;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoObjectRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

/// Pre-render event for miscellaneous animatables being rendered by [GeoObjectRenderer]
///
/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
///
/// This event is cancellable.
/// If the event is cancelled, the entity will not be rendered.
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @param <T> Object animatable class type
/// @param <E> Associated object class type, or [Void] if none
/// @param <R> RenderState class type
/// @see GeoRenderEvent
/// @see Pre
public class GeoObjectPreRenderEvent<T extends GeoAnimatable, E, R extends GeoRenderState> implements GeoRenderEvent.Object.Pre<T, E, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
        for (Listener<?, ?, ?> listener : listeners) {
            if (!listener.handle(event))
                return false;
        }

        return true;
    });
    private final RenderPassInfo<R> renderPassInfo;
    private final SubmitNodeCollector renderTasks;

    public GeoObjectPreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        this.renderPassInfo = renderPassInfo;
        this.renderTasks = renderTasks;
    }

    @Override
    public RenderPassInfo<R> getRenderPassInfo() {
        return this.renderPassInfo;
    }

    @Override
    public GeoObjectRenderer<T, E, R> getRenderer() {
        return (GeoObjectRenderer)this.renderPassInfo.renderer();
    }

    @ApiStatus.Internal
    @Override
    public R getRenderState() {
        return this.renderPassInfo.renderState();
    }

    @Override
    public PoseStack getPoseStack() {
        return this.renderPassInfo.poseStack();
    }

    @Override
    public BakedGeoModel getModel() {
        return this.renderPassInfo.model();
    }

    @Override
    public SubmitNodeCollector getRenderTasks() {
        return this.renderTasks;
    }

    @Override
    public CameraRenderState getCameraState() {
        return this.renderPassInfo.cameraState();
    }

    /// Event listener interface for the [Object.Pre] GeoRenderEvent
    ///
    /// Return false to cancel the render pass
    ///
    /// @param <T> Object animatable class type
    /// @param <E> Associated object class type, or [Void] if none
    /// @param <R> RenderState class type
    @FunctionalInterface
    public interface Listener<T extends GeoAnimatable, E, R extends GeoRenderState> {
        boolean handle(GeoObjectPreRenderEvent<T, E, R> event);
    }
}
