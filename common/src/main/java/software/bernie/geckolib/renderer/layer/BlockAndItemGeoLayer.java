package software.bernie.geckolib.renderer.layer;

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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * {@link GeoRenderLayer} for rendering {@link BlockState BlockStates}
 * or {@link ItemStack ItemStacks} on a given {@link GeoAnimatable}
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
    public abstract void addRenderData(T animatable, O relatedObject, R renderState, float partialTick);

    /**
     * Container for data needed to render an item or block for a bone.
     *
     * @param boneName The name of the bone to render the armor piece for
     * @param displayContext The {@link ItemDisplayContext} to use when rendering the item
     * @param retrievalFunction The function to retrieve the {@link ItemStack} or {@link BlockState} to render. You probably need to override
     * {@link #addRenderData(GeoAnimatable, Object, GeoRenderState, float)} as well
     */
    public record RenderData<R extends GeoRenderState>(String boneName, ItemDisplayContext displayContext, BiFunction<GeoBone, R, Either<@NotNull ItemStack, @NotNull BlockState>> retrievalFunction) {}


    /**
     * Register per-bone render operations, to be rendered after the main model is done.
     * <p>
     * Even though the task is called after the main model renders, the {@link PoseStack} provided will be posed as if the bone
     * is currently rendering.
     *
     * @param consumer The registrar to accept the per-bone render tasks
     */
    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, boolean didRenderModel, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (!didRenderModel)
            return;

        for (RenderData<R> renderData : getRelevantBones(renderState, model)) {
            model.getBone(renderData.boneName).ifPresentOrElse(bone -> createPerBoneRender(bone, renderData, consumer, renderState), () ->
                    GeckoLibConstants.LOGGER.error("Unable to find bone for ItemArmorGeoLayer: {}, skipping", renderData.boneName));
        }
    }

    private void createPerBoneRender(GeoBone bone, RenderData<R> renderData, BiConsumer<GeoBone, PerBoneRender<R>> consumer, R renderState) {
        Either<ItemStack, BlockState> renderObject = renderData.retrievalFunction().apply(bone, renderState);

        renderObject.ifLeft(stack -> {
            if (!stack.isEmpty()) {
                consumer.accept(bone, (renderState2, poseStack, bone2, renderTasks, cameraState,
                                       packedLight, packedOverlay, renderColor) -> {
                    //RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                    renderStackForBone(poseStack, bone2, stack, renderData.displayContext, renderState2, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
                });
            }
        }).ifRight(blockState -> {
            if (!blockState.isAir()) {
                consumer.accept(bone, (renderState2, poseStack, bone2, renderTasks, cameraState,
                                       packedLight, packedOverlay, renderColor) -> {
                    //RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                    renderBlockForBone(poseStack, bone2, blockState, renderState2, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
                });
            }
        });
    }

    /**
     * Render the given {@link ItemStack} for the provided {@link GeoBone}.
     */
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks,
                                      CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        final ItemStackRenderState stackRenderState = new ItemStackRenderState();
        final Minecraft mc = Minecraft.getInstance();

        mc.getItemModelResolver().updateForTopItem(stackRenderState, stack, displayContext, mc.level, null, (int)(long)renderState.getOrDefaultGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, 0L) + displayContext.ordinal());
        stackRenderState.submit(poseStack, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, 0);
    }

    /**
     * Render the given {@link BlockState} for the provided {@link GeoBone}.
     */
    protected void renderBlockForBone(PoseStack poseStack, GeoBone bone, BlockState state, R renderState, SubmitNodeCollector renderTasks,
                                      CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();
        poseStack.translate(-0.25f, -0.25f, -0.25f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        renderTasks.submitBlock(poseStack, state, packedLight, OverlayTexture.NO_OVERLAY, renderState instanceof EntityRenderState entityState ? entityState.outlineColor : 0);
        poseStack.popPose();
    }
}
