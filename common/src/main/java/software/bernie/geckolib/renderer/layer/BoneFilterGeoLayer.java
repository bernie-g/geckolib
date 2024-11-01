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
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * {@link GeoRenderLayer} for auto-applying some form of modification to bones of a model prior to rendering
 * <p>
 * This can be useful for enabling or disabling bone rendering based on arbitrary conditions
 * <p>
 * NOTE: Despite this layer existing, it is much more efficient to use {@link FastBoneFilterGeoLayer} instead
 */
public class BoneFilterGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
	protected final TriConsumer<GeoBone, T, Float> checkAndApply;

	public BoneFilterGeoLayer(GeoRenderer<T> renderer) {
		this(renderer, (bone, animatable, partialTick) -> {});
	}

	public BoneFilterGeoLayer(GeoRenderer<T> renderer, TriConsumer<GeoBone, T, Float> checkAndApply) {
		super(renderer);

		this.checkAndApply = checkAndApply;
	}

	/**
	 * This method is called for each bone in the model
	 * <p>
	 * Check whether the bone should be affected and apply the modification as needed.
	 */
	protected void checkAndApply(GeoBone bone, T animatable, float partialTick) {
		this.checkAndApply.accept(bone, animatable, partialTick);
	}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int renderColor) {
		for (GeoBone bone : bakedModel.topLevelBones()) {
			checkChildBones(bone, animatable, partialTick);
		}
	}

	private void checkChildBones(GeoBone parentBone, T animatable, float partialTick) {
		checkAndApply(parentBone, animatable, partialTick);

		for (GeoBone bone : parentBone.getChildBones()) {
			checkChildBones(bone, animatable, partialTick);
		}
	}
}
