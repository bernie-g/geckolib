package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.util.TriConsumer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * {@link GeoRenderLayer} for auto-applying some form of modification to bones of a model prior to rendering.<br>
 * This can be useful for enabling or disabling bone rendering based on arbitrary conditions.<br>
 * <br>
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
	 * This method is called for each bone in the model.<br>
	 * Check whether the bone should be affected and apply the modification as needed.
	 */
	protected void checkAndApply(GeoBone bone, T animatable, float partialTick) {
		this.checkAndApply.accept(bone, animatable, partialTick);
	}

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
