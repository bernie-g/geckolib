package com.geckolib.event.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

/// Pre-render event for entities being rendered by [GeoEntityRenderer]
///
/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
///
/// This event is [cancellable][ICancellableEvent].
/// If the event is cancelled, the entity will not be rendered.
///
/// **<u>NOTE:</u>** Some methods on this event are not overridden in this class. Check [GeoRenderEvent]
///
/// @see GeoRenderEvent
/// @see Pre
public class GeoEntityPreRenderEvent<T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Event implements GeoRenderEvent.Entity.Pre<T, R>, ICancellableEvent {
    private final RenderPassInfo<R> renderPassInfo;
    private final SubmitNodeCollector renderTasks;

    public GeoEntityPreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        this.renderPassInfo = renderPassInfo;
        this.renderTasks = renderTasks;
    }

    @Override
    public RenderPassInfo<R> getRenderPassInfo() {
        return this.renderPassInfo;
    }

    @Override
    public GeoEntityRenderer<T, R> getRenderer() {
        return (GeoEntityRenderer)this.renderPassInfo.renderer();
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
}
