package software.bernie.geckolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.armor.CompileArmorRenderLayersEvent;
import software.bernie.geckolib.event.armor.CompileArmorRenderStateEvent;
import software.bernie.geckolib.event.armor.GeoArmorPostRenderEvent;
import software.bernie.geckolib.event.armor.GeoArmorPreRenderEvent;
import software.bernie.geckolib.event.block.CompileBlockRenderLayersEvent;
import software.bernie.geckolib.event.block.CompileBlockRenderStateEvent;
import software.bernie.geckolib.event.block.GeoBlockPostRenderEvent;
import software.bernie.geckolib.event.block.GeoBlockPreRenderEvent;
import software.bernie.geckolib.event.entity.CompileEntityRenderLayersEvent;
import software.bernie.geckolib.event.entity.CompileEntityRenderStateEvent;
import software.bernie.geckolib.event.entity.GeoEntityPostRenderEvent;
import software.bernie.geckolib.event.entity.GeoEntityPreRenderEvent;
import software.bernie.geckolib.event.item.CompileItemRenderLayersEvent;
import software.bernie.geckolib.event.item.CompileItemRenderStateEvent;
import software.bernie.geckolib.event.item.GeoItemPostRenderEvent;
import software.bernie.geckolib.event.item.GeoItemPreRenderEvent;
import software.bernie.geckolib.event.object.CompileObjectRenderLayersEvent;
import software.bernie.geckolib.event.object.CompileObjectRenderStateEvent;
import software.bernie.geckolib.event.object.GeoObjectPostRenderEvent;
import software.bernie.geckolib.event.object.GeoObjectPreRenderEvent;
import software.bernie.geckolib.event.replacedentity.CompileReplacedEntityRenderLayersEvent;
import software.bernie.geckolib.event.replacedentity.CompileReplacedEntityRenderStateEvent;
import software.bernie.geckolib.event.replacedentity.GeoReplacedEntityPostRenderEvent;
import software.bernie.geckolib.event.replacedentity.GeoReplacedEntityPreRenderEvent;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.service.GeckoLibEvents;

/**
 * Fabric service implementation for GeckoLib's various events
 */
public class GeckoLibEventsFabric implements GeckoLibEvents {
    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderLayers} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> void fireCompileBlockRenderLayers(
            GeoBlockRenderer<T, R> renderer) {
        CompileBlockRenderLayersEvent.EVENT.invoker().handle(new CompileBlockRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderState} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> void fireCompileBlockRenderState(
            GeoBlockRenderer<T, R> renderer, R renderState, T animatable) {
        CompileBlockRenderStateEvent.EVENT.invoker().handle(new CompileBlockRenderStateEvent<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> boolean fireBlockPreRender(
            GeoBlockRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        return GeoBlockPreRenderEvent.EVENT.invoker().handle(new GeoBlockPreRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Post} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> void fireBlockPostRender(
            GeoBlockRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoBlockPostRenderEvent.EVENT.invoker().handle(new GeoBlockPostRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> void fireCompileArmorRenderLayers(
            GeoArmorRenderer<T, R> renderer) {
        CompileArmorRenderLayersEvent.EVENT.invoker().handle(new CompileArmorRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState & GeoRenderState> void fireCompileArmorRenderState(
            GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData) {
        CompileArmorRenderStateEvent.EVENT.invoker().handle(new CompileArmorRenderStateEvent<>(renderer, renderState, animatable, renderData));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> boolean fireArmorPreRender(
            GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        return GeoArmorPreRenderEvent.EVENT.invoker().handle(new GeoArmorPreRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Post} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> void fireArmorPostRender(
            GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoArmorPostRenderEvent.EVENT.invoker().handle(new GeoArmorPostRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderLayers} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireCompileEntityRenderLayers(GeoEntityRenderer<T, R> renderer) {
        CompileEntityRenderLayersEvent.EVENT.invoker().handle(new CompileEntityRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderState} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireCompileEntityRenderState(
            GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
        CompileEntityRenderStateEvent.EVENT.invoker().handle(new CompileEntityRenderStateEvent<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> boolean fireEntityPreRender(
            GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        return GeoEntityPreRenderEvent.EVENT.invoker().handle(new GeoEntityPreRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Post} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireEntityPostRender(
            GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoEntityPostRenderEvent.EVENT.invoker().handle(new GeoEntityPostRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> void fireCompileReplacedEntityRenderLayers(
            GeoReplacedEntityRenderer<T, E, R> renderer) {
        CompileReplacedEntityRenderLayersEvent.EVENT.invoker().handle(new CompileReplacedEntityRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> void fireCompileReplacedEntityRenderState(
            GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity) {
        CompileReplacedEntityRenderStateEvent.EVENT.invoker().handle(new CompileReplacedEntityRenderStateEvent<>(renderer, renderState, animatable, entity));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> boolean fireReplacedEntityPreRender(
            GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        return GeoReplacedEntityPreRenderEvent.EVENT.invoker().handle(new GeoReplacedEntityPreRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Post} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> void fireReplacedEntityPostRender(
            GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoReplacedEntityPostRenderEvent.EVENT.invoker().handle(new GeoReplacedEntityPostRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoAnimatable> void fireCompileItemRenderLayers(
            GeoItemRenderer<T> renderer) {
        CompileItemRenderLayersEvent.EVENT.invoker().handle(new CompileItemRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderState} event
     */
    @Override
    public <T extends Item & GeoAnimatable, O extends GeoItemRenderer.RenderData, R extends GeoRenderState> void fireCompileItemRenderState(
            GeoItemRenderer<T> renderer, R renderState, T animatable, O renderData) {
        CompileItemRenderStateEvent.EVENT.invoker().handle(new CompileItemRenderStateEvent<>(renderer, renderState, animatable, renderData));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends Item & GeoAnimatable, R extends GeoRenderState> boolean fireItemPreRender(
            GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        return GeoItemPreRenderEvent.EVENT.invoker().handle(new GeoItemPreRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Post} event
     */
    @Override
    public <T extends Item & GeoAnimatable, R extends GeoRenderState> void fireItemPostRender(
            GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoItemPostRenderEvent.EVENT.invoker().handle(new GeoItemPostRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState> void fireCompileObjectRenderLayers(
            GeoObjectRenderer<T, E, R> renderer) {
        CompileObjectRenderLayersEvent.EVENT.invoker().handle(new CompileObjectRenderLayersEvent<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState> void fireCompileObjectRenderState(
            GeoObjectRenderer<T, E, R> renderer, R renderState, T animatable, @Nullable E relatedObject) {
        CompileObjectRenderStateEvent.EVENT.invoker().handle(new CompileObjectRenderStateEvent<>(renderer, renderState, animatable, relatedObject));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Pre} event, returning true if the event was not cancelled
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState> boolean fireObjectPreRender(
            GeoObjectRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        return GeoObjectPreRenderEvent.EVENT.invoker().handle(new GeoObjectPreRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Post} event
     */
    @Override
    public <T extends GeoAnimatable, E, R extends GeoRenderState> void fireObjectPostRender(
            GeoObjectRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        GeoObjectPostRenderEvent.EVENT.invoker().handle(new GeoObjectPostRenderEvent<>(renderer, renderState, poseStack, model, renderTasks, cameraState));
    }
}
