package software.bernie.geckolib.cache.model;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.loading.json.raw.ModelProperties;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Baked model object for GeckoLib models
 *
 * @param topLevelBones The root bone(s) for this model, as defined in the model .json
 * @param properties The additional properties collection for the model. These aren't typically used by GeckoLib itself, and are just here for end-users if needed
 * @param boneLookup A deferred lookup cache of every bone by its name for quick-retrieval
 */
public record BakedGeoModel(GeoBone[] topLevelBones, ModelProperties properties, Supplier<Map<String, GeoBone>> boneLookup) {
	public BakedGeoModel(GeoBone[] topLevelBones, ModelProperties properties) {
		this(topLevelBones, properties, createBoneMap(topLevelBones));
	}

	/**
	 * Gets a bone from this model by name
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		return Optional.ofNullable(this.boneLookup.get().get(name));
	}

    /**
     * Render this model
     */
    public <R extends GeoRenderState> void render(RenderPassInfo<R> renderPassInfo, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {
        final PoseStack poseStack = renderPassInfo.poseStack();

        for (GeoBone bone : topLevelBones()) {
            poseStack.pushPose();
            RenderUtil.prepMatrixForBone(poseStack, bone);
            bone.updateBonePositionListeners(poseStack, renderPassInfo);

            bone.render(renderPassInfo, poseStack, vertexConsumer, packedLight, packedOverlay, renderColor);
            bone.renderChildren(renderPassInfo, poseStack, vertexConsumer, packedLight, packedOverlay, renderColor);

            poseStack.popPose();
        }
    }

	/**
	 * Create the bone map for this model, memoizing it as most models won't need it at all
	 */
	private static Supplier<Map<String, GeoBone>> createBoneMap(GeoBone[] topLevelBones) {
		return Suppliers.memoize(() -> {
			Object2ReferenceMap<String, GeoBone> boneMap = new Object2ReferenceOpenHashMap<>();

			for (GeoBone bone : topLevelBones) {
				boneMap.put(bone.name(), bone);

				for (GeoBone child : collectChildBones(bone)) {
					boneMap.put(child.name(), child);
				}
			}

			return boneMap;
		});
	}

	/**
	 * Recursively collect all child bones of a bone
	 */
	private static List<GeoBone> collectChildBones(GeoBone bone) {
		List<GeoBone> bones = new ObjectArrayList<>();

		for (GeoBone child : bone.children()) {
			bones.add(child);
			bones.addAll(collectChildBones(child));
		}

		return bones;
	}
}
