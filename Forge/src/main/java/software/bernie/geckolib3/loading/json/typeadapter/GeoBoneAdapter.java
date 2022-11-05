package software.bernie.geckolib3.loading.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import software.bernie.geckolib3.geo.render.built.GeoBone;

import java.lang.reflect.Type;

/**
 * {@link com.google.gson.Gson} {@link JsonDeserializer} for {@link GeoBone GeoBones}.<br>
 * Acts as the deserialization interface for {@code GeoBones}
 */
public class GeoBoneAdapter implements JsonDeserializer<GeoBone> {
	@Override
	public GeoBone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return null;
	}
}
