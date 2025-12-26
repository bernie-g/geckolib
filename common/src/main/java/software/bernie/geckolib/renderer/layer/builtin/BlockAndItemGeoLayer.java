package software.bernie.geckolib.renderer.layer.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * {@link GeoRenderLayer} for rendering {@link BlockState BlockStates}
 * or {@link ItemStack ItemStacks} on a given {@link GeoAnimatable}
 *
 * @param <T> Animatable class type. Inherited from the renderer this layer is attached to
 * @param <O> Associated object class type, or {@link Void} if none. Inherited from the renderer this layer is attached to
 * @param <R> RenderState class type. Inherited from the renderer this layer is attached to
 */
public abstract class BlockAndItemGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public BlockAndItemGeoLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }

    /**
     * Return a list of the bone names that this layer will render for.
     * <p>
     * Ideally, you would cache this list in a class-field if you don't need any data from the input renderState or model
     */
    protected abstract List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model);

    /**
     * Override to add any custom {@link DataTicket}s you need to capture for rendering.
     * <p>
     * The animatable is discarded from the rendering context after this, so any data needed
     * for rendering should be captured in the renderState provided
     *
     * @param animatable The animatable instance being rendered
     * @param relatedObject An object related to the render pass or null if not applicable.
     *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
     * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
     * @param partialTick The fraction of a tick that has elapsed as of the current render pass
     */
    @Override
    public abstract void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick);

    /**
     * Container for data needed to render an item or block for a bone.
     *
     * @param boneName The name of the bone to render the armor piece for
     * @param displayContext The {@link ItemDisplayContext} to use when rendering the item
     * @param retrievalFunction The function to retrieve the {@link ItemStack} or {@link BlockState} to render. You probably need to override
     * {@link GeoRenderLayer#addRenderData(GeoAnimatable, Object, GeoRenderState, float)} as well
     */
    public record RenderData<R extends GeoRenderState>(String boneName, ItemDisplayContext displayContext, BiFunction<GeoBone, R, Either<ItemStack, BlockState>> retrievalFunction) {}

    /**
     * Register per-bone render operations, to be rendered after the main model is done.
     * <p>
     * Even though the task is called after the main model renders, the {@link PoseStack} provided will be posed as if the bone
     * is currently rendering.
     *
     * @param renderPassInfo The collated render-related data for this render pass
     * @param consumer The registrar to accept the per-bone render tasks
     */
    @Override
    public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (!renderPassInfo.willRender())
            return;

        final R renderState = renderPassInfo.renderState();
        final BakedGeoModel model = renderPassInfo.model();

        for (RenderData<R> renderData : getRelevantBones(renderState, model)) {
            model.getBone(renderData.boneName)
                    .ifPresentOrElse(bone -> createPerBoneRender(bone, renderData, consumer, renderState),
                                     () -> GeckoLibConstants.LOGGER.error("Unable to find bone for ItemArmorGeoLayer: {}, skipping", renderData.boneName));
        }

    }

    private void createPerBoneRender(GeoBone bone, RenderData<R> renderData, BiConsumer<GeoBone, PerBoneRender<R>> consumer, R renderState) {
        Either<ItemStack, BlockState> renderObject = renderData.retrievalFunction().apply(bone, renderState);

        renderObject.ifLeft(stack -> {
            if (!stack.isEmpty()) {
                consumer.accept(bone, (renderPassInfo, bone2, renderTasks) -> {
                    //RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                    submitItemStackRender(renderPassInfo.poseStack(), bone2, stack, renderData.displayContext, renderPassInfo.renderState(), renderTasks,
                                          renderPassInfo.cameraState(), renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderPassInfo.renderColor());
                });
            }
        }).ifRight(blockState -> {
            if (!blockState.isAir()) {
                consumer.accept(bone, (renderPassInfo, bone2, renderTasks) -> {
                    //RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                    submitBlockRender(renderPassInfo.poseStack(), bone2, blockState, renderPassInfo.renderState(), renderTasks,
                                      renderPassInfo.cameraState(), renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderPassInfo.renderColor());
                });
            }
        });
    }

    /**
     * Render the given {@link ItemStack} for the provided {@link GeoBone}.
     */
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks,
                                         CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        final ItemStackRenderState stackRenderState = new ItemStackRenderState();
        final Minecraft mc = Minecraft.getInstance();

        mc.getItemModelResolver().updateForTopItem(stackRenderState, stack, displayContext, mc.level, null, (int)(long)renderState.getOrDefaultGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, 0L) + displayContext.ordinal());
        stackRenderState.submit(poseStack, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, 0);
    }

    /**
     * Render the given {@link BlockState} for the provided {@link GeoBone}.
     */
    protected void submitBlockRender(PoseStack poseStack, GeoBone bone, BlockState state, R renderState, SubmitNodeCollector renderTasks,
                                     CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();
        poseStack.translate(-0.25f, -0.25f, -0.25f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderTasks.submitBlock(poseStack, state, packedLight, OverlayTexture.NO_OVERLAY, renderState instanceof EntityRenderState entityState ? entityState.outlineColor : 0);
        poseStack.popPose();
    }
}
