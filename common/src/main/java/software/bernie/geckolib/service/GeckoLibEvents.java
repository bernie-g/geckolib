package software.bernie.geckolib.service;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/**
 * Loader-agnostic service interface for GeckoLib's various events
 */
public interface GeckoLibEvents {
    /**
     * Fire the Block.CompileRenderLayers event
     */
    <T extends BlockEntity & GeoAnimatable>
    void fireCompileBlockRenderLayers(GeoBlockRenderer<T> renderer);
    /**
     * Fire the Armor.CompileRenderState event
     */
    <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    void fireCompileBlockRenderState(GeoBlockRenderer<T> renderer, R renderState, T animatable);
    /**
     * Fire the Block.Pre event
     */
    <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    boolean fireBlockPreRender(GeoBlockRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
    /**
     * Fire the Block.Post event
     */
    <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    void fireBlockPostRender(GeoBlockRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);

    /**
     * Fire the Armor.CompileRenderLayers event
     */
    <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderLayers(GeoArmorRenderer<T, R> renderer);
    /**
     * Fire the Armor.CompileRenderState event
     */
    <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderState(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData);
    /**
     * Fire the Armor.Pre event
     */
    <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    boolean fireArmorPreRender(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
    /**
     * Fire the Armor.Post event
     */
    <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireArmorPostRender(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);

    /**
     * Fire the Entity.CompileRenderLayers event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireCompileEntityRenderLayers(GeoEntityRenderer<T, R> renderer);
    /**
     * Fire the Entity.CompileRenderState event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireCompileEntityRenderState(GeoEntityRenderer<T, R> renderer, R renderState, T animatable);
    /**
     * Fire the Entity.Pre event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    boolean fireEntityPreRender(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
    /**
     * Fire the Entity.Post event
     */
    <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireEntityPostRender(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);

    /**
     * Fire the ReplacedEntity.CompileRenderLayers event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<T, E, R> renderer);
    /**
     * Fire the ReplacedEntity.CompileRenderState event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderState(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity);
    /**
     * Fire the ReplacedEntity.Pre event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    boolean fireReplacedEntityPreRender(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
    /**
     * Fire the ReplacedEntity.Post event
     */
    <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireReplacedEntityPostRender(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);

    /**
     * Fire the Item.CompileRenderLayers event
     */
    <T extends Item & GeoAnimatable>
    void fireCompileItemRenderLayers(GeoItemRenderer<T> renderer);
    /**
     * Fire the Item.CompileRenderState event
     */
    <T extends Item & GeoAnimatable, O extends GeoItemRenderer.RenderData, R extends GeoRenderState>
    void fireCompileItemRenderState(GeoItemRenderer<T> renderer, R renderState, T animatable, O renderData);
    /**
     * Fire the Item.Pre event
     */
    <T extends Item & GeoAnimatable, R extends GeoRenderState>
    boolean fireItemPreRender(GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
    /**
     * Fire the Item.Post event
     */
    <T extends Item & GeoAnimatable, R extends GeoRenderState>
    void fireItemPostRender(GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);

    /**
     * Fire the Object.CompileRenderLayers event
     */
    <T extends GeoAnimatable>
    void fireCompileObjectRenderLayers(GeoObjectRenderer<T> renderer);
    /**
     * Fire the Object.CompileRenderState event
     */
    <T extends GeoAnimatable, R extends GeoRenderState>
    void fireCompileObjectRenderState(GeoObjectRenderer<T> renderer, R renderState, T animatable);
    /**
     * Fire the Object.Pre event
     */
    <T extends GeoAnimatable, R extends GeoRenderState>
    boolean fireObjectPreRender(GeoObjectRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
    /**
     * Fire the Object.Post event
     */
    <T extends GeoAnimatable, R extends GeoRenderState>
    void fireObjectPostRender(GeoObjectRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
}
