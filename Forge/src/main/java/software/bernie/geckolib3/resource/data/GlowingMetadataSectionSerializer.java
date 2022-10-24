package software.bernie.geckolib3.resource.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import software.bernie.geckolib3.resource.data.GlowingMetadataSection.Section;
import software.bernie.geckolib3.util.json.JsonUtil;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Serializer for emissive texture config data
 * 
 * Originally developed for chocolate quest repoured
 */
public class GlowingMetadataSectionSerializer implements IMetadataSectionSerializer<GlowingMetadataSection> {

	@Override
	public String getMetadataSectionName() {
		return "glowsections";
	}

	@Override
	public GlowingMetadataSection fromJson(JsonObject jsonobject) {
		if (jsonobject.has("sections")) {
			JsonArray jsonarray = JSONUtils.convertToJsonArray(jsonobject.get("sections"), "sections");
			GlowingMetadataSection result = new GlowingMetadataSection(
					JsonUtil.stream(jsonarray, JsonObject.class).map(jsonObj -> {
						final int x1 = JSONUtils.getAsInt(jsonObj, "x1", JSONUtils.getAsInt(jsonObj, "x", 0));
						final int y1 = JSONUtils.getAsInt(jsonObj, "y1", JSONUtils.getAsInt(jsonObj, "y", 0));
						final int width = JSONUtils.getAsInt(jsonObj, "w", 0);
						final int height = JSONUtils.getAsInt(jsonObj, "h", 0);
						final int x2 = JSONUtils.getAsInt(jsonObj, "x2", x1 + width);
						final int y2 = JSONUtils.getAsInt(jsonObj, "y2", y1 + height);
						return new Section(x1, y1, x2, y2);
					}));
			if (!result.isEmpty()) {
				return result;
			}
		}
		return null;
	}

}
