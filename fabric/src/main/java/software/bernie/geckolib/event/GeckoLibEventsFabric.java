package software.bernie.geckolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
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
    public <T extends BlockEntity & GeoAnimatable>
    void fireCompileBlockRenderLayers(GeoBlockRenderer<T> renderer) {
        GeoRenderEvent.Block.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Block.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.CompileRenderState} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    void fireCompileBlockRenderState(GeoBlockRenderer<T> renderer, R renderState, T animatable) {
        GeoRenderEvent.Block.CompileRenderState.EVENT.invoker().handle(new GeoRenderEvent.Block.CompileRenderState<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Pre} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    boolean fireBlockPreRender(GeoBlockRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return GeoRenderEvent.Block.Pre.EVENT.invoker().handle(new GeoRenderEvent.Block.Pre<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Block.Post} event
     */
    @Override
    public <T extends BlockEntity & GeoAnimatable, R extends GeoRenderState>
    void fireBlockPostRender(GeoBlockRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        GeoRenderEvent.Block.Post.EVENT.invoker().handle(new GeoRenderEvent.Block.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderLayers(GeoArmorRenderer<T, R> renderer) {
        GeoRenderEvent.Armor.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Armor.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event
     */
    @Override
    public <T extends Item & GeoItem, O extends GeoArmorRenderer.RenderData, R extends HumanoidRenderState & GeoRenderState>
    void fireCompileArmorRenderState(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, O renderData) {
        GeoRenderEvent.Armor.CompileRenderState.EVENT.invoker().handle(new GeoRenderEvent.Armor.CompileRenderState<>(renderer, renderState, animatable, renderData));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Pre} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    boolean fireArmorPreRender(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return GeoRenderEvent.Armor.Pre.EVENT.invoker().handle(new GeoRenderEvent.Armor.Pre<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Armor.Post} event
     */
    @Override
    public <T extends Item & GeoItem, R extends HumanoidRenderState & GeoRenderState>
    void fireArmorPostRender(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        GeoRenderEvent.Armor.Post.EVENT.invoker().handle(new GeoRenderEvent.Armor.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderLayers} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> void fireCompileEntityRenderLayers(GeoEntityRenderer<T, R> renderer) {
        GeoRenderEvent.Entity.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Entity.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.CompileRenderState} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireCompileEntityRenderState(GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
        GeoRenderEvent.Entity.CompileRenderState.EVENT.invoker().handle(new GeoRenderEvent.Entity.CompileRenderState<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Pre} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    boolean fireEntityPreRender(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return GeoRenderEvent.Entity.Pre.EVENT.invoker().handle(new GeoRenderEvent.Entity.Pre<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Entity.Post} event
     */
    @Override
    public <T extends Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState>
    void fireEntityPostRender(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        GeoRenderEvent.Entity.Post.EVENT.invoker().handle(new GeoRenderEvent.Entity.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<T, E, R> renderer) {
        GeoRenderEvent.ReplacedEntity.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireCompileReplacedEntityRenderState(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, T animatable, E entity) {
        GeoRenderEvent.ReplacedEntity.CompileRenderState.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.CompileRenderState<>(renderer, renderState, animatable, entity));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Pre} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    boolean fireReplacedEntityPreRender(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return GeoRenderEvent.ReplacedEntity.Pre.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.Pre<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.ReplacedEntity.Post} event
     */
    @Override
    public <T extends GeoAnimatable, E extends Entity, R extends EntityRenderState & GeoRenderState>
    void fireReplacedEntityPostRender(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        GeoRenderEvent.ReplacedEntity.Post.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderLayers} event
     */
    @Override
    public <T extends Item & GeoAnimatable>
    void fireCompileItemRenderLayers(GeoItemRenderer<T> renderer) {
        GeoRenderEvent.Item.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Item.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.CompileRenderState} event
     */
    @Override
    public <T extends Item & GeoAnimatable, O extends ItemStack, R extends GeoRenderState>
    void fireCompileItemRenderState(GeoItemRenderer<T> renderer, R renderState, T animatable, O itemStack) {
        GeoRenderEvent.Item.CompileRenderState.EVENT.invoker().handle(new GeoRenderEvent.Item.CompileRenderState<>(renderer, renderState, animatable, itemStack));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Pre} event
     */
    @Override
    public <T extends Item & GeoAnimatable, R extends GeoRenderState>
    boolean fireItemPreRender(GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return GeoRenderEvent.Item.Pre.EVENT.invoker().handle(new GeoRenderEvent.Item.Pre<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Item.Post} event
     */
    @Override
    public <T extends Item & GeoAnimatable, R extends GeoRenderState>
    void fireItemPostRender(GeoItemRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        GeoRenderEvent.Item.Post.EVENT.invoker().handle(new GeoRenderEvent.Item.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderLayers} event
     */
    @Override
    public <T extends GeoAnimatable>
    void fireCompileObjectRenderLayers(GeoObjectRenderer<T> renderer) {
        GeoRenderEvent.Object.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Object.CompileRenderLayers<>(renderer));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.CompileRenderState} event
     */
    @Override
    public <T extends GeoAnimatable, R extends GeoRenderState>
    void fireCompileObjectRenderState(GeoObjectRenderer<T> renderer, R renderState, T animatable) {
        GeoRenderEvent.Object.CompileRenderState.EVENT.invoker().handle(new GeoRenderEvent.Object.CompileRenderState<>(renderer, renderState, animatable));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Pre} event
     */
    @Override
    public <T extends GeoAnimatable, R extends GeoRenderState>
    boolean fireObjectPreRender(GeoObjectRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
        return GeoRenderEvent.Object.Pre.EVENT.invoker().handle(new GeoRenderEvent.Object.Pre<>(renderer, renderState, poseStack, model, bufferSource));
    }

    /**
     * Fire the {@link GeoRenderEvent.Object.Post} event
     */
    @Override
    public <T extends GeoAnimatable, R extends GeoRenderState>
    void fireObjectPostRender(GeoObjectRenderer<T> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
       GeoRenderEvent.Object.Post.EVENT.invoker().handle(new GeoRenderEvent.Object.Post<>(renderer, renderState, poseStack, model, bufferSource));
    }
}
