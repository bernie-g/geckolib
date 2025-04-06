package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * {@link GeoRenderLayer} for auto-applying some form of modification to bones of a model prior to rendering
 * <p>
 * This can be useful for enabling or disabling bone rendering based on arbitrary conditions
 * <p>
 * NOTE: Despite this layer existing, it is much more efficient to use {@link FastBoneFilterGeoLayer} instead
 */
public class BoneFilterGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
	protected final TriConsumer<GeoBone, R, Float> checkAndApply;

	public BoneFilterGeoLayer(GeoRenderer<T, O, R> renderer) {
		this(renderer, (bone, animatable, partialTick) -> {});
	}

	public BoneFilterGeoLayer(GeoRenderer<T, O, R> renderer, TriConsumer<GeoBone, R, Float> checkAndApply) {
		super(renderer);

		this.checkAndApply = checkAndApply;
	}

	/**
	 * This method is called for each bone in the model
	 * <p>
	 * Check whether the bone should be affected and apply the modification as needed.
	 */
	protected void checkAndApply(GeoBone bone, R renderState, float partialTick) {
		this.checkAndApply.accept(bone, renderState, partialTick);
	}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	@Override
	public void preRender(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource,
						  @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		float partialTick = renderState.getGeckolibData(DataTickets.PARTIAL_TICK);

		for (GeoBone bone : bakedModel.topLevelBones()) {
			checkChildBones(bone, renderState, partialTick);
		}
	}

	private void checkChildBones(GeoBone parentBone, R renderState, float partialTick) {
		checkAndApply(parentBone, renderState, partialTick);

		for (GeoBone bone : parentBone.getChildBones()) {
			checkChildBones(bone, renderState, partialTick);
		}
	}
}
