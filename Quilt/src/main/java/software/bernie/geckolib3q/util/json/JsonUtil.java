package software.bernie.geckolib3q.util.json;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * needed for emissive texture
 * 
 * Originally developed for chocolate quest repoured
 */
public class JsonUtil {

	public static <T extends JsonElement> Stream<T> stream(JsonArray jsonArray, Class<T> jsonClass) {
		return IntStream.range(0, jsonArray.size())
				.mapToObj(jsonArray::get)
				.filter(jsonClass::isInstance)
				.map(jsonClass::cast);
	}

}
