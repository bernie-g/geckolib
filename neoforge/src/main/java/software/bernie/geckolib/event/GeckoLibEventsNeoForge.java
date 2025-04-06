package software.bernie.geckolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.service.GeckoLibEvents;

/**
 * NeoForge service implementation for GeckoLib's various events
 */
public class GeckoLibEventsNeoForge implements GeckoLibEvents {
    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderLayers} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable>
    void fireCompileBlockRenderLayers(GeoBlockRenderer<T> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderState} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    void fireCompileBlockRenderState(GeoBlockRenderer<T> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.CompileRenderState<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Pre} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    boolean fireBlockPreRender(GeoBlockRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.Pre<>(renderer, renderState, poseStack, model, bufferSource)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Post} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    void fireBlockPostRender(GeoBlockRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderLayers(GeoArmorRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event
     */
    @Override
    public <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderState(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.CompileRenderState<>(renderer, renderState, animatable, renderData));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    boolean fireArmorPreRender(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.Pre<>(renderer, renderState, poseStack, model, bufferSource)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Post} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireArmorPostRender(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderLayers} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireCompileEntityRenderLayers(GeoEntityRenderer<T, R> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderState} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireCompileEntityRenderState(GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.CompileRenderState<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Pre} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    boolean fireEntityPreRender(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.Pre<>(renderer, renderState, poseStack, model, bufferSource)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Post} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireEntityPostRender(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<T, E, R> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderState(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.CompileRenderState<>(renderer, renderState, animatable, entity));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Pre} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    boolean fireReplacedEntityPreRender(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.Pre<>(renderer, renderState, poseStack, model, bufferSource)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Post} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireReplacedEntityPostRender(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoAnimatable>
    void fireCompileItemRenderLayers(GeoItemRenderer<T> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderState} event
     */
    @Override
    public <T extends Item & GeoAnimatable, O extends ItemStack, R extends GeoRenderState>
    void fireCompileItemRenderState(GeoItemRenderer<T> renderer, R renderState, T animatable, O itemStack) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.CompileRenderState<>(renderer, renderState, animatable, itemStack));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Pre} event
     */
    @Override
    public <T extends Item & GeoAnimatable, R extends GeoRenderState>
    boolean fireItemPreRender(GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.Pre<>(renderer, renderState, poseStack, model, bufferSource)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Post} event
     */
    @Override
    public <T extends Item & GeoAnimatable, R extends GeoRenderState>
    void fireItemPostRender(GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable>
    void fireCompileObjectRenderLayers(GeoObjectRenderer<T> renderer) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, R extends GeoRenderState>
    void fireCompileObjectRenderState(GeoObjectRenderer<T> renderer, R renderState, T animatable) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.CompileRenderState<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Pre} event
     */
    @Override
    public <T extends GeoAnimatable, R extends GeoRenderState>
    boolean fireObjectPreRender(GeoObjectRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.Pre<>(renderer, renderState, poseStack, model, bufferSource)).isCanceled();
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Post} event
     */
    @Override
    public <T extends GeoAnimatable, R extends GeoRenderState>
    void fireObjectPostRender(GeoObjectRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }
}
