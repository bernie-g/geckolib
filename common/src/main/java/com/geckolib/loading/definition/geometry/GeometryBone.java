package com.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoLocator;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.util.JsonUtil;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/// Container class for a single geometry bone, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// @param name The name of this bone
/// @param parent The parent bone of this bone, if any
/// @param pivot The pivot point for this bone, or null if not defined
/// @param rotation The rotation of this bone, in degrees, or null if not defined
/// @param debug An optional debug marker for this bone. Not used by GeckoLib
/// @param mirror An optional mirror toggle for this bone
/// @param inflate An optional inflation value for this bone
/// @param renderGroupId The numerical group index this bone belongs to. Not used by GeckoLib
/// @param cubes The array of cube definitions for this bone
/// @param binding An optional binding for this bone, defining its parental relationship. Not used by GeckoLib
/// @param locators An optional map of locator markers for this bone
/// @param polyMesh An optional poly mesh definition for this bone. Not used by GeckoLib
/// @param textureMeshes An optional array of texture mesh definitions for this bone. Not used by GeckoLib
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record GeometryBone(String name, @Nullable String parent, @Nullable Vec3 pivot, @Nullable Vec3 rotation, boolean debug,
                           @Nullable Boolean mirror, @Nullable Float inflate, int renderGroupId, GeometryCube @Nullable [] cubes,
                           @Nullable String binding, @Nullable Map<String, GeometryLocator> locators, @Nullable GeometryPolyMesh polyMesh,
                           GeometryTextureMesh @Nullable[] textureMeshes) {
    /// Parse a GeometryBone instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryBone> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final String name = GsonHelper.getAsString(obj, "name");
            final String parent = GsonHelper.getAsString(obj, "parent", null);
            final Vec3 pivot = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "pivot", null));
            final Vec3 rotation = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "rotation", null));
            final boolean debug = GsonHelper.getAsBoolean(obj, "debug", false);
            final Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
            final Float inflate = JsonUtil.getOptionalFloat(obj, "inflate");
            final int renderGroupId = GsonHelper.getAsInt(obj, "render_group_id", 0);
            final GeometryCube[] cubes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "cubes", new JsonArray()), context, GeometryCube.class);
            final String binding = GsonHelper.getAsString(obj, "binding", null);
            final Map<String, GeometryLocator> locators = JsonUtil.jsonObjToMap(GsonHelper.getAsJsonObject(obj, "locators", null), context, GeometryLocator.class);
            final GeometryPolyMesh polyMesh = GsonHelper.getAsObject(obj, "poly_mesh", null, context, GeometryPolyMesh.class);
            final GeometryTextureMesh[] textureMeshes = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "texture_meshes", null), context, GeometryTextureMesh.class);

            return new GeometryBone(name, parent, pivot, rotation, debug, mirror, inflate, renderGroupId, cubes, binding, locators, polyMesh, textureMeshes);
        };
    }

    /// Bake this `GeometryBone` instance into the final [GeoBone] instance that GeckoLib uses for rendering
    public GeoBone bake(@Nullable GeoBone parentBone, GeometryDescription geometryDescription, Map<String, List<GeometryBone>> childBonesMap, BiConsumer<GeoBone, GeoLocator> locatorConsumer) {
        final Vec3 pivot = this.pivot == null ? Vec3.ZERO : this.pivot;
        final Vec3 rotation = this.rotation == null ? Vec3.ZERO : this.rotation;
        final List<GeometryBone> children = childBonesMap.getOrDefault(this.name, List.of());
        final GeoBone[] childBones = new GeoBone[children.size()];
        final GeoCube[] cubes = new GeoCube[this.cubes == null ? 0 : this.cubes.length];
        final GeoLocator[] locators = new GeoLocator[this.locators == null ? 0 : this.locators.size()];
        final GeoBone bone = new CuboidGeoBone(parentBone, this.name, childBones, cubes, locators, (float)-pivot.x, (float)pivot.y, (float)pivot.z,
                                            (float)Math.toRadians(-rotation.x), (float)Math.toRadians(-rotation.y), (float)Math.toRadians(rotation.z));

        fillLocators(bone, locators, locatorConsumer);
        fillCubes(cubes, geometryDescription);
        fillChildBones(bone, geometryDescription, children, childBonesMap, childBones, locatorConsumer);

        return bone;
    }

    /// Bake the [GeometryLocator]s on this bone into their [GeoLocator] equivalents and fill the locators array
    private void fillLocators(GeoBone bone, GeoLocator[] locatorsArray, BiConsumer<GeoBone, GeoLocator> locatorConsumer) {
        if (this.locators == null || locatorsArray.length == 0)
            return;

        int locatorIndex = 0;

        for (Map.Entry<String, GeometryLocator> locatorEntry : this.locators.entrySet()) {
            GeoLocator locator = locatorEntry.getValue().bake(locatorEntry.getKey(), bone);
            locatorsArray[locatorIndex++] = locator;

            locatorConsumer.accept(bone, locator);
        }
    }

    /// Bake the [GeometryCube]s on this bone into their [GeoCube] equivalents and fill the cubes array
    private void fillCubes(GeoCube[] cubesArray, GeometryDescription geometryDescription) {
        if (this.cubes == null || cubesArray.length == 0)
            return;

        for (int i = 0; i < cubesArray.length && i < this.cubes.length; i++) {
            cubesArray[i] = this.cubes[i].bake(this, geometryDescription);
        }
    }

    /// Bake the [GeometryBone] children on this bone into their [GeoBone] equivalents and fill the child bones array
    private void fillChildBones(GeoBone bone, GeometryDescription geometryDescription, List<GeometryBone> children,
                                Map<String, List<GeometryBone>> childBonesMap, GeoBone[] childBones, BiConsumer<GeoBone, GeoLocator> locatorConsumer) {
        int i = 0;

        for (GeometryBone child : children) {
            childBones[i++] = child.bake(bone, geometryDescription, childBonesMap, locatorConsumer);
        }
    }
}
