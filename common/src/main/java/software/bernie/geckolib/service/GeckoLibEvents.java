package software.bernie.geckolib.service;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Loader-agnostic service interface for GeckoLib's various events
 */
public interface GeckoLibEvents {
    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderLayers} event
     */
    <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> void fireCompileBlockRenderLayers(
            GeoBlockRenderer<T, R> renderer);
    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderState} event
     */
    <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> void fireCompileBlockRenderState(
            GeoBlockRenderer<T, R> renderer, R renderState, T animatable);
    /**
     * Fire the {@link GeoRenderEvent.Block.Pre} event, returning true if the event was not cancelled
     */
    <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> boolean fireBlockPreRender(
            GeoBlockRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
    /**
     * Fire the {@link GeoRenderEvent.Block.Post} event
     */
    <T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> void fireBlockPostRender(
            GeoBlockRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderLayers} event
     */
    <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> void fireCompileArmorRenderLayers(
            GeoArmorRenderer<T, R> renderer);
    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderState} event
     */
    <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState & GeoRenderState> void fireCompileArmorRenderState(
            GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData);
    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event, returning true if the event was not cancelled
     */
    <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> boolean fireArmorPreRender(
            GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
    /**
     * Fire the {@link GeoRenderEvent.Armor.Post} event
     */
    <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> void fireArmorPostRender(
            GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderLayers} event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireCompileEntityRenderLayers(
            GeoEntityRenderer<T, R> renderer);
    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderState} event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireCompileEntityRenderState(
            GeoEntityRenderer<T, R> renderer, R renderState, T animatable);
    /**
     * Fire the {@link GeoRenderEvent.Entity.Pre} event, returning true if the event was not cancelled
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> boolean fireEntityPreRender(
            GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
    /**
     * Fire the {@link GeoRenderEvent.Entity.Post} event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireEntityPostRender(
            GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> void fireCompileReplacedEntityRenderLayers(
            GeoReplacedEntityRenderer<T, E, R> renderer);
    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderState} event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> void fireCompileReplacedEntityRenderState(
            GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity);
    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Pre} event, returning true if the event was not cancelled
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> boolean fireReplacedEntityPreRender(
            GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Post} event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState> void fireReplacedEntityPostRender(
            GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderLayers} event
     */
    <T extends Item & GeoAnimatable> void fireCompileItemRenderLayers(
            GeoItemRenderer<T> renderer);
    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderState} event
     */
    <T extends Item & GeoAnimatable, O extends GeoItemRenderer.RenderData, R extends GeoRenderState> void fireCompileItemRenderState(
            GeoItemRenderer<T> renderer, R renderState, T animatable, O renderData);
    /**
     * Fire the {@link GeoRenderEvent.Item.Pre} event, returning true if the event was not cancelled
     */
    <T extends Item & GeoAnimatable, R extends GeoRenderState> boolean fireItemPreRender(
            GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
    /**
     * Fire the {@link GeoRenderEvent.Item.Post} event
     */
    <T extends Item & GeoAnimatable, R extends GeoRenderState> void fireItemPostRender(
            GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderLayers} event
     */
    <T extends GeoAnimatable, E, R extends GeoRenderState> void fireCompileObjectRenderLayers(
            GeoObjectRenderer<T, E, R> renderer);
    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderState} event
     */
    <T extends GeoAnimatable, E, R extends GeoRenderState> void fireCompileObjectRenderState(
            GeoObjectRenderer<T, E, R> renderer, R renderState, T animatable, @Nullable E relatedObject);
    /**
     * Fire the {@link GeoRenderEvent.Object.Pre} event, returning true if the event was not cancelled
     */
    <T extends GeoAnimatable, E, R extends GeoRenderState> boolean fireObjectPreRender(
            GeoObjectRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
    /**
     * Fire the {@link GeoRenderEvent.Object.Post} event
     */
    <T extends GeoAnimatable, E, R extends GeoRenderState> void fireObjectPostRender(
            GeoObjectRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);
}
