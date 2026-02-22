package com.geckolib.loading.definition.geometry;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.util.JsonUtil;

/// Container class for a single geometry definition, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This is not a 1:1 parity container for the specification, as GeckoLib intentionally discards properties that have no possible uses
///
/// @param description Associated geometry definition data for this geometry instance. Technically not optional, but GeckoLib allows it anyway
/// @param cape An optional cape identifier. Not used by GeckoLib
/// @param bones The bones definition for this geometry instance
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record GeometryDefinition(@Nullable GeometryDescription description, @Nullable String cape, GeometryBone @Nullable [] bones) {
    /// Parse a GeometryDefinition instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryDefinition> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonObject obj = json.getAsJsonObject();
            final GeometryDescription description = GsonHelper.getAsObject(obj, "description", null, context, GeometryDescription.class);
            final String cape = GsonHelper.getAsString(obj, "cape", null);
            final GeometryBone[] bones = JsonUtil.jsonArrayToObjectArray(GsonHelper.getAsJsonArray(obj, "bones", new JsonArray(0)), context, GeometryBone.class);

            if (!obj.has("description"))
                GeckoLibConstants.LOGGER.warn("No geometry description found in model file, likely an invalid geometry json!");

            return new GeometryDefinition(description, cape, bones);
        };
    }
}
