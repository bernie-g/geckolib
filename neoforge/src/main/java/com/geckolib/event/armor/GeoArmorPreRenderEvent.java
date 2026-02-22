package com.geckolib.event.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoItem;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

/**
 * Pre-render event for armor pieces being rendered by {@link GeoArmorRenderer}
 * <p>
 * This event is called before rendering, but after {@link GeoRenderer#preRenderPass}
 * <p>
 * This event is {@link ICancellableEvent cancellable}.<br>
 * If the event is cancelled, the armor piece will not be rendered.
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see Pre
 */
public class GeoArmorPreRenderEvent<T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Event implements GeoRenderEvent.Armor.Pre<T, R>, ICancellableEvent {
    private final RenderPassInfo<R> renderPassInfo;
    private final SubmitNodeCollector renderTasks;

    public GeoArmorPreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        this.renderPassInfo = renderPassInfo;
        this.renderTasks = renderTasks;
    }

    @Override
    public RenderPassInfo<R> getRenderPassInfo() {
        return this.renderPassInfo;
    }

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
        return (GeoArmorRenderer)this.renderPassInfo.renderer();
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
