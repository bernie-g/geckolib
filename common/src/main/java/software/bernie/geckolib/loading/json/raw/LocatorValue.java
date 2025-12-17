package software.bernie.geckolib.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.util.JsonUtil;

/**
 * Container class for locator value information, only used in deserialization at startup
 *
 * @param locatorClass The locator class for this value
 * @param values The values for this locator type
 */
public record LocatorValue(@Nullable LocatorClass locatorClass, double[] values) {
	public static JsonDeserializer<LocatorValue> deserializer() throws JsonParseException {
		return (json, type, context) -> {
			if (json.isJsonArray()) {
				return new LocatorValue(null, JsonUtil.jsonArrayToDoubleArray(json.getAsJsonArray()));
			}
			else if (json.isJsonObject()) {
				return new LocatorValue(context.deserialize(json.getAsJsonObject(), LocatorClass.class), new double[0]);
			}
			else {
				throw new JsonParseException("Invalid format for LocatorValue in json");
			}
		};
	}
}
