package software.bernie.geckolib.event.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Pre-render event for armor pieces being rendered by {@link GeoArmorRenderer}
 * <p>
 * This event is called before rendering, but after {@link GeoRenderer#preRender}
 * <p>
 * This event is {@link ICancellableEvent cancellable}.<br>
 * If the event is cancelled, the armor piece will not be rendered and the corresponding {@link Post} event will not be fired.
 * <p>
 * <b><u>NOTE:</u></b> Some methods on this event are not overridden in this class. Check {@link GeoRenderEvent}
 *
 * @see GeoRenderEvent
 * @see Pre
 */
public class GeoArmorPreRenderEvent<T extends Item & GeoItem, R extends AvatarRenderState & GeoRenderState> extends Event implements GeoRenderEvent.Armor.Pre<T, R>, ICancellableEvent {
    private final GeoArmorRenderer<T, R> renderer;
    private final R renderState;
    private final PoseStack poseStack;
    private final BakedGeoModel model;
    private final SubmitNodeCollector renderTasks;
    private final CameraRenderState cameraState;

    public GeoArmorPreRenderEvent(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        this.renderer = renderer;
        this.renderState = renderState;
        this.poseStack = poseStack;
        this.model = model;
        this.renderTasks = renderTasks;
        this.cameraState = cameraState;
    }

    @Override
    public GeoArmorRenderer<T, R> getRenderer() {
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
