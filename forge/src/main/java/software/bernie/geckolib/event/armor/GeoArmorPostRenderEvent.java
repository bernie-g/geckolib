package software.bernie.geckolib.event.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Post-render event for armor pieces being rendered by {@link GeoArmorRenderer}
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
public record GeoArmorPostRenderEvent<T extends Item & GeoItem, R extends AvatarRenderState & GeoRenderState>
        (GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState)
        implements GeoRenderEvent.Armor.Post<T, R>, RecordEvent {
    public static final EventBus<GeoArmorPostRenderEvent> BUS = EventBus.create(GeoArmorPostRenderEvent.class);

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
