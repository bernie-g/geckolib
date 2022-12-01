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

import java.util.List;
import java.util.function.Supplier;

/**
 * A more efficient version of {@link BoneFilterGeoLayer}.<br>
 * This version requires you provide the list of bones to filter up-front,
 * so that the bone hierarchy doesn't need to be traversed.
 */
public class FastBoneFilterGeoLayer<T extends GeoAnimatable> extends BoneFilterGeoLayer<T> {
	protected final Supplier<List<String>> boneSupplier;

	public FastBoneFilterGeoLayer(GeoRenderer<T> renderer) {
		this(renderer, List::of);
	}

	public FastBoneFilterGeoLayer(GeoRenderer<T> renderer, Supplier<List<String>> boneSupplier) {
		this(renderer, boneSupplier, (bone, animatable, partialTick) -> {});
	}

	public FastBoneFilterGeoLayer(GeoRenderer<T> renderer, Supplier<List<String>> boneSupplier, TriConsumer<GeoBone, T, Float> checkAndApply) {
		super(renderer, checkAndApply);

		this.boneSupplier = boneSupplier;
	}

	/**
	 * Return a list of bone names to grab to then be filtered.<br>
	 * This is even more efficient if you use a cached list.
	 */
	protected List<String> getAffectedBones() {
		return boneSupplier.get();
	};

	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource,
						  VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (String boneName : getAffectedBones()) {
			this.renderer.getGeoModel().getBone(boneName).ifPresent(bone -> checkAndApply(bone, animatable, partialTick));
		}
	}
}
