package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import software.bernie.geckolib.util.JsonUtil;

/// Container class for UV information, only used in deserialization at startup
public record UVUnion(Either<double[], UVFaces> uvData) {
	/// @return Whether this UVUnion is Box-UV style
	public boolean isBoxUV() {
		return this.uvData.left().isPresent();
	}

	public static JsonDeserializer<UVUnion> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			if (json.isJsonObject()) {
				return new UVUnion(Either.right(context.deserialize(json.getAsJsonObject(), UVFaces.class)));
			}
			else if (json.isJsonArray()) {
				return new UVUnion(Either.left(JsonUtil.jsonArrayToDoubleArray(json.getAsJsonArray())));
			}
			else {
				throw new JsonParseException("Invalid format provided for UVUnion, must be either double array or UVFaces collection");
			}
		};
	}
}
