package com.geckolib.event.replacedentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.event.GeoRenderEvent;
import com.geckolib.renderer.GeoReplacedEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;

/**
 * Pre-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer}
 * <p>
 * This event is called before rendering, but after {@link GeoRenderer#preRenderPass}
 * <p>
 * This event is cancellable.<br>
 * If the event is cancelled, the entity will not be rendered.
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @param <T> Entity animatable class type. This is the animatable being rendered
 * @param <E> Entity class type. This is the entity being replaced
 * @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
 * @see GeoRenderEvent
 * @see Pre
 */
public class GeoReplacedEntityPreRenderEvent<T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> implements GeoRenderEvent.ReplacedEntity.Pre<T, E, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
        for (Listener<?, ?, ?> listener : listeners) {
            if (!listener.handle(event))
                return false;
        }

        return true;
    });
    private final RenderPassInfo<R> renderPassInfo;
    private final SubmitNodeCollector renderTasks;

    public GeoReplacedEntityPreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        this.renderPassInfo = renderPassInfo;
        this.renderTasks = renderTasks;
    }

    @Override
    public RenderPassInfo<R> getRenderPassInfo() {
        return this.renderPassInfo;
    }

    @Override
    public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
        return (GeoReplacedEntityRenderer)this.renderPassInfo.renderer();
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

    /**
     * Event listener interface for the {@link ReplacedEntity.Pre} GeoRenderEvent
     * <p>
     * Return false to cancel the render pass
     *
     * @param <T> Entity animatable class type. This is the animatable being rendered
     * @param <E> Entity class type. This is the entity being replaced
     * @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
     */
    @FunctionalInterface
    public interface Listener<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> {
        boolean handle(GeoReplacedEntityPreRenderEvent<T, E, R> event);
    }
}
