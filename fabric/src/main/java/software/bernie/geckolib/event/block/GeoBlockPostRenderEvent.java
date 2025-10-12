package software.bernie.geckolib.event.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Post-render event for block entities being rendered by {@link GeoBlockRenderer}
 * <p>
 * This event is called after {@link GeoRenderer#postRender}
 * <p>
 * Because of the batching Minecraft uses for rendering, nothing has actually been rendered at this stage, and further rendering operations should be
 * submitted to the {@link SubmitNodeCollector} returned by {@link #getRenderTasks()}.
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see Post
 */
public class GeoBlockPostRenderEvent<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> implements GeoRenderEvent.Block.Post<T, R> {
    public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
        for (Listener<?, ?> listener : listeners) {
            listener.handle(event);
        }
    });
    private final GeoBlockRenderer<T, R> renderer;
    private final R renderState;
    private final PoseStack poseStack;
    private final BakedGeoModel model;
    private final SubmitNodeCollector renderTasks;
    private final CameraRenderState cameraState;

    public GeoBlockPostRenderEvent(GeoBlockRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.poseStack = poseStack;
        this.model = model;
        this.renderTasks = renderTasks;
        this.cameraState = cameraState;
    }

    @Override
    public GeoBlockRenderer<T, R> getRenderer() {
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

    /**
     * Event listener interface for the {@link Block.Post} GeoRenderEvent
     */
    @FunctionalInterface
    public interface Listener<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> {
        void handle(GeoBlockPostRenderEvent<T, R> event);
    }
}
