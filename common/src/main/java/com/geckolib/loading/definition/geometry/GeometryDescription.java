package com.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.cache.model.ModelProperties;
import com.geckolib.util.JsonUtil;

/// Container class for geometry properties, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This is not a 1:1 parity container for the specification, as GeckoLib intentionally discards properties that have no possible uses
///
/// @param identifier The asset identifier for this model. Not used by GeckoLib
/// @param visibleBoundsWidth The width of the visible bounds for this model. Not used by GeckoLib
/// @param visibleBoundsHeight The height of the visible bounds for this model. Not used by GeckoLib
/// @param visibleBoundsOffset The offset of the visible bounds for this model. Not used by GeckoLib
/// @param textureWidth The width of the texture for this model. Technically optional, but GeckoLib requires it
/// @param textureHeight The height of the texture for this model. Technically optional, but GeckoLib requires it
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record GeometryDescription(String identifier, @Nullable Float visibleBoundsWidth, @Nullable Float visibleBoundsHeight, @Nullable Vec3 visibleBoundsOffset, int textureWidth, int textureHeight) {
	public static final GeometryDescription EMPTY = new GeometryDescription("geometry.unknown", null, null, null, 16, 16);

	/// Parse a GeometryDescription instance from raw .json input via [Gson]
	public static JsonDeserializer<GeometryDescription> gsonDeserializer() throws JsonParseException {
		return (json, type, context) -> {
			final JsonObject obj = json.getAsJsonObject();
			final String identifier = GsonHelper.getAsString(obj, "identifier", null);
			final Float visibleBoundsWidth = JsonUtil.getOptionalFloat(obj, "visible_bounds_width");
			final Float visibleBoundsHeight = JsonUtil.getOptionalFloat(obj, "visible_bounds_height");
			final Vec3 visibleBoundsOffset = JsonUtil.jsonToVec3(GsonHelper.getAsJsonArray(obj, "visible_bounds_offset", null));
			final int textureWidth = GsonHelper.getAsInt(obj, "texture_width", 16);
			final int textureHeight = GsonHelper.getAsInt(obj, "texture_height", 16);

			if (!obj.has("texture_width") || !obj.has("texture_height"))
				GeckoLibConstants.LOGGER.warn("GeckoLib model {} does not have texture dimensions specified, likely an invalid geometry json!", identifier);

			return new GeometryDescription(identifier == null ? String.valueOf(obj.hashCode()) : identifier,
										   visibleBoundsWidth, visibleBoundsHeight, visibleBoundsOffset, textureWidth, textureHeight);
		};
	}

	/// Bake this Geometry instance into the final [ModelProperties] instance that GeckoLib uses for rendering
	public ModelProperties bake(Identifier resourcePath) {
		return new ModelProperties(resourcePath, this.identifier, this.visibleBoundsWidth, this.visibleBoundsHeight, this.visibleBoundsOffset, this.textureWidth, this.textureHeight);
	}
}
