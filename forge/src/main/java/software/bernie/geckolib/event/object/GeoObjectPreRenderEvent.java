package software.bernie.geckolib.event.object;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Pre-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}
 * <p>
 * This event is called before rendering, but after {@link GeoRenderer#preRender}
 * <p>
 * This event is {@link Cancellable cancellable}.<br>
 * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see Pre
 */
public record GeoObjectPreRenderEvent<T extends GeoAnimatable, E, R extends GeoRenderState>
        (GeoObjectRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState)
        implements GeoRenderEvent.Object.Pre<T, E, R>, RecordEvent, Cancellable {
    public static final CancellableEventBus<GeoObjectPreRenderEvent> BUS = CancellableEventBus.create(GeoObjectPreRenderEvent.class);

    @Override
    public GeoObjectRenderer<T, E, R> getRenderer() {
        return this.renderer;
    }

    @ApiStatus.Internal
    @Override
    public R getRenderState() {
        return this.renderState;
    }

    @Override
    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    @Override
    public BakedGeoModel getModel() {
        return this.model;
    }

    @Override
    public SubmitNodeCollector getRenderTasks() {
        return this.renderTasks;
    }

    @Override
    public CameraRenderState getCameraState() {
        return this.cameraState;
    }
}
