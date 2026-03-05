package com.geckolib.renderer.layer.builtin;

import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

/// [GeoRenderLayer] for rendering [BlockStates][BlockState]
/// or [ItemStacks][ItemStack] on a given [GeoAnimatable]
///
/// @param <T> Animatable class type. Inherited from the renderer this layer is attached to
/// @param <O> Associated object class type, or [Void] if none. Inherited from the renderer this layer is attached to
/// @param <R> RenderState class type. Inherited from the renderer this layer is attached to
public abstract class BlockAndItemGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public static final DataTicket<List<RenderData>> CONTENTS = DataTicket.create("blockanditemgeolayer_contents", new TypeToken<>() {});
    protected ItemModelResolver itemModelResolver;
    protected BlockModelResolver blockModelResolver;

    public BlockAndItemGeoLayer(EntityRendererProvider.Context context, GeoRenderer<T, O, R> renderer) {
        super(renderer);

        this.itemModelResolver = context.getItemModelResolver();
        this.blockModelResolver = context.getBlockModelResolver();
    }

    /// Return a list of [RenderData] instances to render
    protected abstract List<RenderData> getRelevantBones(T animatable, @Nullable O relatedObject, R renderState, float partialTick);

    /// Override to add any custom [DataTicket]s you need to capture for rendering.
    ///
    /// The animatable is discarded from the rendering context after this, so any data needed
    /// for rendering should be captured in the renderState provided
    ///
    /// `BlockAndItemGeoLayer` relies on the [#CONTENTS] `DataTicket` having been set in this method
    ///
    /// @param animatable The animatable instance being rendered
    /// @param relatedObject An object related to the render pass or null if not applicable.
    ///                         (E.G., ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
    /// @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
    /// @param partialTick The fraction of a tick that has elapsed as of the current render pass
    @Override
    public abstract void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick);

    /// Container for data needed to render an item or block for a bone.
    ///
    /// @param boneName The name of the bone to render at
    /// @param displayContext The [ItemDisplayContext] to use when rendering an item
    /// @param modelState The [ItemStackRenderState] or [BlockModelRenderState] to render. You probably need to override
    /// [GeoRenderLayer#addRenderData(GeoAnimatable, Object, GeoRenderState, float)] as well
    public record RenderData(String boneName, ItemDisplayContext displayContext, Either<ItemStackRenderState, BlockModelRenderState> modelState) {
        /// Create a new [RenderData] instance for an [ItemStack]
        public static RenderData item(String boneName, ItemDisplayContext displayContext, ItemStackRenderState modelState) {
            return new RenderData(boneName, displayContext, Either.left(modelState));
        }

        /// Create a new [RenderData] instance for a [BlockState]
        public static RenderData block(String boneName, BlockModelRenderState modelState) {
            return new RenderData(boneName, ItemDisplayContext.NONE, Either.right(modelState));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /// Register per-bone render operations, to be rendered after the main model is done.
    ///
    /// Even though the task is called after the main model renders, the [PoseStack] provided will be posed as if the bone
    /// is currently rendering.
    ///
    /// @param renderPassInfo The collated render-related data for this render pass
    /// @param consumer The registrar to accept the per-bone render tasks
    @Override
    public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (!renderPassInfo.willRender())
            return;

        final R renderState = renderPassInfo.renderState();
        final BakedGeoModel model = renderPassInfo.model();

        for (RenderData renderData : renderPassInfo.renderState().getOrDefaultGeckolibData(CONTENTS, List.of())) {
            model.getBone(renderData.boneName)
                    .ifPresentOrElse(bone -> createPerBoneRender(bone, renderData, consumer, renderState),
                                     () -> GeckoLibConstants.LOGGER.error("Unable to find bone for ItemArmorGeoLayer: {}, skipping", renderData.boneName));
        }

    }

    private void createPerBoneRender(GeoBone bone, RenderData renderData, BiConsumer<GeoBone, PerBoneRender<R>> consumer, R renderState) {
        renderData.modelState().ifLeft(stack -> {
            if (!stack.isEmpty()) {
                consumer.accept(bone, (renderPassInfo, bone2, renderTasks) -> {
                    submitItemStackRender(renderPassInfo.poseStack(), bone2, stack, renderData.displayContext, renderPassInfo.renderState(), renderTasks, renderPassInfo.packedLight());
                });
            }
        }).ifRight(blockState -> {
            if (!blockState.isEmpty()) {
                consumer.accept(bone, (renderPassInfo, bone2, renderTasks) -> {
                    submitBlockRender(renderPassInfo.poseStack(), bone2, blockState, renderPassInfo.renderState(), renderTasks, renderPassInfo.packedLight());
                });
            }
        });
    }

    /// Render the given [ItemStack] for the provided [GeoBone].
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks, int packedLight) {
        stackState.submit(poseStack, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, renderState instanceof EntityRenderState entityState ? entityState.outlineColor : 0);
    }

    /// Render the given [BlockState] for the provided [GeoBone].
    protected void submitBlockRender(PoseStack poseStack, GeoBone bone, BlockModelRenderState blockState, R renderState, SubmitNodeCollector renderTasks, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.25f, -0.25f, -0.25f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        blockState.submit(poseStack, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, renderState instanceof EntityRenderState entityState ? entityState.outlineColor : 0);
        poseStack.popPose();
    }
    //</editor-fold>
}
