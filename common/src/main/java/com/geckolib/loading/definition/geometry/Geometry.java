package com.geckolib.loading.definition.geometry;

import com.google.common.base.Joiner;
import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.GeckoLibConstants;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoLocator;
import com.geckolib.util.JsonUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/// Container class for a full geometry file definition, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This is the root-level object for a fully processed .geo file
///
/// @param formatVersion The bedrock geometry format version of this geometry instance
/// @param debug An optional debug marker for this geometry instance. Not used by GeckoLib
/// @param definitions The array of geometry definitions contained in this geometry instance
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record Geometry(String formatVersion, boolean debug, GeometryDefinition[] definitions) {
    /// Publicly accessible GSON parser for GeckoLib geometry .json files
    public static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT)
            .registerTypeAdapter(Geometry.class, gsonDeserializer())
            .registerTypeAdapter(GeometryBone.class, GeometryBone.gsonDeserializer())
            .registerTypeAdapter(GeometryCube.class, GeometryCube.gsonDeserializer())
            .registerTypeAdapter(GeometryDefinition.class, GeometryDefinition.gsonDeserializer())
            .registerTypeAdapter(GeometryDescription.class, GeometryDescription.gsonDeserializer())
            .registerTypeAdapter(GeometryLocator.class, GeometryLocator.gsonDeserializer())
            .registerTypeAdapter(GeometryPolyIndex.class, GeometryPolyIndex.gsonDeserializer())
            .registerTypeAdapter(GeometryPolyIndices.class, GeometryPolyIndices.gsonDeserializer())
            .registerTypeAdapter(GeometryPolyMesh.class, GeometryPolyMesh.gsonDeserializer())
            .registerTypeAdapter(GeometryTextureMesh.class, GeometryTextureMesh.gsonDeserializer())
            .registerTypeAdapter(GeometryUv.class, GeometryUv.gsonDeserializer())
            .registerTypeAdapter(GeometryUvMapping.class, GeometryUvMapping.gsonDeserializer())
            .registerTypeAdapter(GeometryUvMappingDetails.class, GeometryUvMappingDetails.gsonDeserializer())
            .registerTypeAdapter(GeometryUvPair.class, GeometryUvPair.gsonDeserializer())
            .create();

    /// Parse a Geometry instance from raw .json input via [Gson]
    public static JsonDeserializer<Geometry> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String version = GsonHelper.getAsString(obj, "format_version");
            final boolean debug = GsonHelper.getAsBoolean(obj, "debug", false);
            final GeometryDefinition[] definitions = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "minecraft:geometry", null), context, GeometryDefinition.class);

            if (definitions == null || definitions.length == 0)
                throw new JsonParseException("No geometry definitions found in model file!");

            return new Geometry(version, debug, definitions);
        };
    }

    /// Bake this `Geometry` instance into the final [BakedGeoModel] instance that GeckoLib uses for rendering
    public BakedGeoModel bake(Identifier resourcePath) throws RuntimeException {
        final GeometryDefinition geometryDefinition = this.definitions[0];
        final GeometryDescription description = geometryDefinition.description();
        final Map<String, GeoLocator> locators = new Object2ReferenceOpenHashMap<>();
        final GeoBone[] topLevelBones = bakeTopLevelBones(geometryDefinition, locators);

        return new BakedGeoModel(topLevelBones, locators, (description == null ? GeometryDescription.EMPTY : description).bake(resourcePath));
    }

    /// Bake the [GeometryBone]s into [GeoBone]s for this `GeometryDefinition`
    private GeoBone[] bakeTopLevelBones(GeometryDefinition definition, Map<String, GeoLocator> locators) {
        if (definition.bones() == null)
            return new GeoBone[0];

        final GeometryDescription geometryDescription = definition.description() == null ? GeometryDescription.EMPTY : definition.description();
        final BonesCollection bonesCollection = sortBones(definition.bones());
        final GeoBone[] topLevelBones = new GeoBone[bonesCollection.size()];
        final BiConsumer<GeoBone, GeoLocator> locatorConsumer = (bone, locator) -> {
            if (locators.put(locator.name(), locator) != null)
                GeckoLibConstants.LOGGER.error("Duplicate locator name found in bone '{}': '{}'", bone.name(), locator.name());
        };

        for (int i = 0; i < topLevelBones.length; i++) {
            GeometryBone geometryBone = bonesCollection.getBone(i);
            GeoBone bone = geometryBone.bake(null, geometryDescription, bonesCollection.childBonesMap(), locatorConsumer);

            topLevelBones[i] = bone;

            for (GeoLocator locator : bone.locators()) {
                locatorConsumer.accept(bone, locator);
            }
        }

        return topLevelBones;
    }

    /// Create sorted collections of GeometryBones to build them into their structured hierarchy
    private BonesCollection sortBones(GeometryBone[] bones) {
        final List<GeometryBone> topLevelBones = new ObjectArrayList<>();
        final Map<String, List<GeometryBone>> childBones = new Object2ObjectOpenHashMap<>();

        for (GeometryBone bone : bones) {
            if (bone.parent() == null) {
                topLevelBones.add(bone);
            }
            else {
                childBones.computeIfAbsent(bone.parent(), _ -> new ObjectArrayList<>())
                        .add(bone);
            }
        }

        return new BonesCollection(bones, topLevelBones, childBones);
    }

    /// Holder object to work with a sorted bone map
    ///
    /// Mostly just to make the code more legible
    private record BonesCollection(GeometryBone[] allBones, List<GeometryBone> topLevelBones, Map<String, List<GeometryBone>> childBonesMap) {
        BonesCollection {
            validateBoneStructure(allBones, childBonesMap);
        }

        /// @return The GeometryBone at the given index
        public GeometryBone getBone(int index) {
            return this.topLevelBones.get(index);
        }

        /// @return The size of the bones collection
        public int size() {
            return this.topLevelBones.size();
        }

        /// Validate the compiled bone structure to check for orphan bones and recursive references
        private void validateBoneStructure(GeometryBone[] allBones, Map<String, List<GeometryBone>> childBones) throws IllegalArgumentException {
            final Set<String> bones = new HashSet<>(allBones.length);

            for (Map.Entry<String, List<GeometryBone>> entry : childBones.entrySet()) {
                final String parent = entry.getKey();

                for (GeometryBone bone : entry.getValue()) {
                    if (parent.equals(bone.name()))
                        throw new IllegalArgumentException("Invalid model definition. Bone has defined itself as its own parent: " + bone.name());
                }
            }

            for (GeometryBone bone : allBones) {
                bones.add(bone.name());
            }

            for (String parentBone : childBones.keySet()) {
                if (!bones.contains(parentBone))
                    throw new IllegalArgumentException("Invalid model definition. Found bone with undefined parent (children -> parent): " + Joiner.on(',').join(childBones.get(parentBone)) + " -> " + parentBone);
            }
        }
    }
}
