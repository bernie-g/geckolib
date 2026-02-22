package com.geckolib.loading.definition.geometry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.ApiStatus;

/// Container class for a single geometry bone's polygon indices, only used for intermediary steps between .json deserialization and GeckoLib object creation
///
/// This information isn't used by GeckoLib natively
///
/// @param position The position array index
/// @param normal The normal array index
/// @param scale The scale array index
/// @see <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.21.0?view=minecraft-bedrock-experimental">Bedrock Geometry Spec 1.21.0</a>
@ApiStatus.Internal
public record GeometryPolyIndex(float position, float normal, float scale) {
    /// Parse a GeometryPolyIndex instance from raw .json input via [Gson]
    public static JsonDeserializer<GeometryPolyIndex> gsonDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            final JsonArray array = json.getAsJsonArray();
            final float position = array.get(0).getAsFloat();
            final float normal = array.get(1).getAsFloat();
            final float scale = array.get(2).getAsFloat();

            return new GeometryPolyIndex(position, normal, scale);
        };
    }
}
