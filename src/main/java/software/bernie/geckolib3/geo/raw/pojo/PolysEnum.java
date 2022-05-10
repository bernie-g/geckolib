package software.bernie.geckolib3.geo.raw.pojo;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * If not specifying vertex indices, arrays of data must be a list of tris or
 * quads, set by making this property either "tri_list" or "quad_list"
 */
public enum PolysEnum {
	QUAD_LIST, TRI_LIST;

	@JsonValue
	public String toValue() {
		switch (this) {
		case QUAD_LIST:
			return "quad_list";
		case TRI_LIST:
			return "tri_list";
		}
		return null;
	}

	@JsonCreator
	public static PolysEnum forValue(String value) throws IOException {
		if (value.equals("quad_list"))
			return QUAD_LIST;
		if (value.equals("tri_list"))
			return TRI_LIST;
		throw new IOException("Cannot deserialize PolysEnum");
	}
}
