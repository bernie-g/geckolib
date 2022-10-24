package software.bernie.geckolib3q.resource.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib3q.resource.data.GlowingMetadataSection.Section;
import software.bernie.geckolib3q.util.json.JsonUtil;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Serializer for emissive texture config data
 * 
 * Originally developed for chocolate quest repoured
 */
public class GlowingMetadataSectionSerializer implements MetadataSectionSerializer<GlowingMetadataSection> {

	@Override
	public String getMetadataSectionName() {
		return "glowsections";
	}

	@Override
	public GlowingMetadataSection fromJson(JsonObject jsonobject) {
		if (jsonobject.has("sections")) {
			JsonArray jsonarray = GsonHelper.convertToJsonArray(jsonobject.get("sections"), "sections");
			GlowingMetadataSection result = new GlowingMetadataSection(
					JsonUtil.stream(jsonarray, JsonObject.class).map(jsonObj -> {
						final int x1 = GsonHelper.getAsInt(jsonObj, "x1", GsonHelper.getAsInt(jsonObj, "x", 0));
						final int y1 = GsonHelper.getAsInt(jsonObj, "y1", GsonHelper.getAsInt(jsonObj, "y", 0));
						final int width = GsonHelper.getAsInt(jsonObj, "w", 0);
						final int height = GsonHelper.getAsInt(jsonObj, "h", 0);
						final int x2 = GsonHelper.getAsInt(jsonObj, "x2", x1 + width);
						final int y2 = GsonHelper.getAsInt(jsonObj, "y2", y1 + height);
						return new Section(x1, y1, x2, y2);
					}));
			if (!result.isEmpty()) {
				return result;
			}
		}
		return null;
	}

}
