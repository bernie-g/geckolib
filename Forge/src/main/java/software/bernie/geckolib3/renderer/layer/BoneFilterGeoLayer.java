package software.bernie.geckolib3.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.renderer.GeoRenderer;

/**
 * {@link GeoRenderLayer} for auto-applying some form of modification to bones of a model prior to rendering.<br>
 * This can be useful for enabling or disabling bone rendering based on arbitrary conditions.<br>
 * <br>
 * NOTE: Despite this layer existing, it is much more efficient to use {@link FastBoneFilterGeoLayer} instead
 */
public abstract class BoneFilterGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
	public BoneFilterGeoLayer(GeoRenderer<T> renderer) {
		super(renderer);
	}

	/**
	 * This method is called for each bone in the model.<br>
	 * Check whether the bone should be affected and apply the modification as needed.
	 */
	protected abstract void checkAndApply(GeoBone bone, T animatable, float partialTick);

	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
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
