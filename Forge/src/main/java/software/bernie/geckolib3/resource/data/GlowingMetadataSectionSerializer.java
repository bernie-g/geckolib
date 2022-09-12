package software.bernie.geckolib3.resource.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib3.resource.data.GlowingMetadataSection.Section;
import software.bernie.geckolib3.util.json.JsonUtil;

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
			GlowingMetadataSection result = new GlowingMetadataSection(JsonUtil.stream(jsonarray, JsonObject.class)
					.map(jsonObj -> new Section(GsonHelper.getAsInt(jsonObj, "x1"), GsonHelper.getAsInt(jsonObj, "y1"),
							GsonHelper.getAsInt(jsonObj, "x2"), GsonHelper.getAsInt(jsonObj, "y2"))));
			if (!result.isEmpty()) {
				return result;
			}
		}
		return null;
	}

}
