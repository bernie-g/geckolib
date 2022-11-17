package software.bernie.geckolib3.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.renderer.GeoRenderer;

import java.util.List;

/**
 * A more efficient version of {@link BoneFilterGeoLayer}.<br>
 * This version requires you provide the list of bones to filter up-front,
 * so that the bone hierarchy doesn't need to be traversed.
 */
public abstract class FastBoneFilterGeoLayer<T extends GeoAnimatable> extends BoneFilterGeoLayer<T> {
	public FastBoneFilterGeoLayer(GeoRenderer<T> renderer) {
		super(renderer);
	}

	/**
	 * Return a list of bone names to grab to then be filtered.<br>
	 * This is even more efficient if you use a cached list.
	 */
	protected abstract List<String> getAffectedBones();

	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (String boneName : getAffectedBones()) {
			this.renderer.getGeoModel().getBone(boneName).ifPresent(bone -> checkAndApply(bone, animatable, partialTick));
		}
	}
}
