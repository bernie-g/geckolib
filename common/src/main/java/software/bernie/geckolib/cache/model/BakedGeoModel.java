package software.bernie.geckolib.cache.model;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.cache.BakedModelCache;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/// Baked model object for GeckoLib models
@SuppressWarnings("ClassCanBeRecord")
public class BakedGeoModel {
	protected final GeoBone[] topLevelBones;
	protected final Map<String, GeoLocator> locators;
	protected final ModelProperties properties;
	protected final Supplier<Map<String, GeoBone>> boneLookup;

	public BakedGeoModel(GeoBone[] topLevelBones, Map<String, GeoLocator> locators, ModelProperties properties, Supplier<Map<String, GeoBone>> boneLookup) {
		this.topLevelBones = topLevelBones;
		this.locators = locators;
		this.properties = properties;
		this.boneLookup = boneLookup;
	}

	public BakedGeoModel(GeoBone[] topLevelBones, Map<String, GeoLocator> locators, ModelProperties properties) {
		this(topLevelBones, locators, properties, createBoneMap(topLevelBones));
	}

	/// @return The root bone(s) for this model, as defined in the model .json
	public GeoBone[] topLevelBones() {
		return this.topLevelBones;
	}

	/// @return The locators in this model, mapped to their name
	public Map<String, GeoLocator> locators() {
		return this.locators;
	}

	/// @return The additional properties collection for the model. These aren't typically used by GeckoLib itself, and are just here for end-users if needed
	public ModelProperties properties() {
		return this.properties;
	}

	/// @return A deferred lookup cache of every bone by its name for quick-retrieval
	public Supplier<Map<String, GeoBone>> boneLookup() {
		return this.boneLookup;
	}

	/// Gets a bone from this model by name
	///
	/// @param name The name of the bone
	/// @return An [Optional] containing the [GeoBone] if one matches, otherwise an empty Optional
	public Optional<GeoBone> getBone(String name) {
		return Optional.ofNullable(this.boneLookup.get().get(name));
	}

	/// Gets a locator from this model by name
	///
	/// @param name The name of the locator
	/// @return An [Optional] containing the [GeoLocator] if one matches, otherwise an empty Optional
	public Optional<GeoLocator> getLocator(String name) {
		return Optional.ofNullable(this.locators.get(name));
	}

	//<editor-fold defaultstate="collapsed" desc="<Internal Methods>">

	/// @return true if this model is the 'missing model', or false if it is a normal GeckoLib model
	public boolean isMissingno() {
		return this == BakedModelCache.MISSINGNO.get();
	}

    /// Render this model
    public <R extends GeoRenderState> void render(RenderPassInfo<R> renderPassInfo, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {
        for (GeoBone bone : topLevelBones()) {
			bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor);
        }
    }

	/// Create the bone map for this model, memoizing it as most models won't need it at all
	protected static Supplier<Map<String, GeoBone>> createBoneMap(GeoBone[] topLevelBones) {
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

	/// Recursively collect all child bones of a bone
	protected static List<GeoBone> collectChildBones(GeoBone bone) {
		List<GeoBone> bones = new ObjectArrayList<>();

		for (GeoBone child : bone.children()) {
			bones.add(child);
			bones.addAll(collectChildBones(child));
		}

		return bones;
	}
	//</editor-fold>
}
