package com.geckolib.event.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

/// Pre-render event for items being rendered by [GeoItemRenderer]
///
/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
///
/// This event is cancellable.
/// If the event is cancelled, the entity will not be rendered.
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @param <T> Item animatable class type
/// @see GeoRenderEvent
/// @see Pre
public class GeoItemPreRenderEvent<T extends Item & GeoAnimatable> implements GeoRenderEvent.Item.Pre<T> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
        for (Listener<?> listener : listeners) {
            if (!listener.handle(event))
                return false;
        }

        return true;
    });
    private final RenderPassInfo<GeoRenderState> renderPassInfo;
    private final SubmitNodeCollector renderTasks;

    public GeoItemPreRenderEvent(RenderPassInfo<GeoRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        this.renderPassInfo = renderPassInfo;
        this.renderTasks = renderTasks;
    }

    @Override
    public RenderPassInfo<GeoRenderState> getRenderPassInfo() {
        return this.renderPassInfo;
    }

    @Override
    public GeoItemRenderer<T> getRenderer() {
        return (GeoItemRenderer)this.renderPassInfo.renderer();
    }

    @ApiStatus.Internal
    @Override
    public GeoRenderState getRenderState() {
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

    /// Event listener interface for the [Item.Pre] GeoRenderEvent
    ///
    /// Return false to cancel the render pass
    ///
    /// @param <T> Item animatable class type
    @FunctionalInterface
    public interface Listener<T extends net.minecraft.world.item.Item & GeoAnimatable> {
        boolean handle(GeoItemPreRenderEvent<T> event);
    }
}
