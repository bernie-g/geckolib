package software.bernie.geckolib3.resource.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import software.bernie.geckolib3.resource.data.GlowingMetadataSection.Section;
import software.bernie.geckolib3.util.json.JsonUtil;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Serializer for emissive texture config data
 * 
 * Originally developed for chocolate quest repoured
 */
public class GlowingMetadataSectionSerializer implements ResourceMetadataReader<GlowingMetadataSection> {

	@Override
	public String getKey() {
		return "glowsections";
	}

	@Override
	public GlowingMetadataSection fromJson(JsonObject jsonobject) {
		if (jsonobject.has("sections")) {
			JsonArray jsonarray = JsonHelper.asArray(jsonobject.get("sections"), "sections");
			GlowingMetadataSection result = new GlowingMetadataSection(
					JsonUtil.stream(jsonarray, JsonObject.class).map(jsonObj -> {
						final int x1 = JsonHelper.getInt(jsonObj, "x1", JsonHelper.getInt(jsonObj, "x", 0));
						final int y1 = JsonHelper.getInt(jsonObj, "y1", JsonHelper.getInt(jsonObj, "y", 0));
						final int width = JsonHelper.getInt(jsonObj, "w", 0);
						final int height = JsonHelper.getInt(jsonObj, "h", 0);
						final int x2 = JsonHelper.getInt(jsonObj, "x2", x1 + width);
						final int y2 = JsonHelper.getInt(jsonObj, "y2", y1 + height);
						return new Section(x1, y1, x2, y2);
					}));
			if (!result.isEmpty()) {
				return result;
			}
		}
		return null;
	}

}
