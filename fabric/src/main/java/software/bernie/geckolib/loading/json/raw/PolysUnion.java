package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import software.bernie.geckolib.util.JsonUtil;

import javax.annotation.Nullable;

/**
 * Container class for poly union information, only used in deserialization at startup
 */
public record PolysUnion(double[][][] union, @Nullable Type type) {
	public static JsonDeserializer<PolysUnion> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
				return new PolysUnion(new double[0][0][0], context.deserialize(json.getAsJsonPrimitive(), Type.class));
			}
			else if (json.isJsonArray()) {
				JsonArray array = json.getAsJsonArray();
				double[][][] matrix = makeSizedMatrix(array);

				for (int x = 0; x < array.size(); x++) {
					JsonArray xArray = array.get(x).getAsJsonArray();

					for (int y = 0; y < xArray.size(); y++) {
						JsonArray yArray = xArray.get(y).getAsJsonArray();

						matrix[x][y] = JsonUtil.jsonArrayToDoubleArray(yArray);
					}
				}

				return new PolysUnion(matrix, null);
			}
			else {
				throw new JsonParseException("Invalid format for PolysUnion, must be either string or array");
			}
		};
	}

	private static double[][][] makeSizedMatrix(JsonArray array) {
		JsonArray subArray = array.size() > 0 ? array.get(0).getAsJsonArray() : null;
		JsonArray subSubArray = subArray != null && subArray.size() > 0 ? subArray.get(0).getAsJsonArray() : null;
		int ySize = subArray != null ? subArray.size() : 0;
		int zSize = subSubArray != null ? subSubArray.size() : 0;

		return new double[array.size()][ySize][zSize];
	}

	public enum Type {
		@SerializedName(value = "quad_list") QUAD,
		@SerializedName(value = "tri_list") TRI;
	}
}
