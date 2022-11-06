package software.bernie.geckolib3.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

import java.util.List;

/**
 * Metadata class that stores the glowing sections of GeckoLib's emissive texture feature for a given texture
 */
public class GeoGlowingTextureMeta {
	public static final MetadataSectionSerializer<GeoGlowingTextureMeta> DESERIALIZER = new MetadataSectionSerializer<>() {
		@Override
		public String getMetadataSectionName() {
			return "glowsections";
		}

		@Override
		public GeoGlowingTextureMeta fromJson(JsonObject json) {
			JsonArray sectionsArray = GsonHelper.getAsJsonArray(json, "sections", null);

			if (sectionsArray == null)
				return null;

			List<Section> list = new ObjectArrayList<>(sectionsArray.size());

			for (JsonElement element : sectionsArray) {
				if (!(element instanceof JsonObject obj))
					throw new JsonParseException("Invalid glowsections json format, expected a JsonObject, found: " + element.getClass());

				int x1 = GsonHelper.getAsInt(obj, "x1", GsonHelper.getAsInt(obj, "x", 0));
				int y1 = GsonHelper.getAsInt(obj, "y1", GsonHelper.getAsInt(obj, "y", 0));
				int x2 = GsonHelper.getAsInt(obj, "x2", GsonHelper.getAsInt(obj, "w", 0) + x1);
				int y2 = GsonHelper.getAsInt(obj, "y2", GsonHelper.getAsInt(obj, "h", 0) + y1);

				if (x1 + y1 + x2 + y2 == 0)
					throw new IllegalArgumentException("Invalid glowsections section object, section must be at least one pixel in size");

				list.add(new Section(x1, y1, x2, y2));
			}

			return new GeoGlowingTextureMeta(list);
		}
	};

	private final List<Section> sections;

	public GeoGlowingTextureMeta(List<Section> sections) {
		this.sections = sections;
	}

	/**
	 * Remap the color channels of the input image to mask for only the applicable pixels.
	 */
	public void createImageMask(NativeImage originalImage, NativeImage newImage) {
		for (Section section : sections) {
			section.createImageMask(originalImage, newImage);
		}
	}

	/**
	 * Section object that contains the rectangular bounds of a glowing texture section on a given sprite
	 */
	public record Section(int x1, int y1, int x2, int y2) {
		/**
		 * Remap the color channels of the input image to mask for only the applicable pixels.
		 */
		public void createImageMask(NativeImage originalImage, NativeImage newImage) {
			for (int x = this.x1; x < this.x2; x++) {
				for (int y = this.y1; y < this.y2; y++) {
					newImage.setPixelRGBA(x, y, originalImage.getPixelRGBA(x, y));
					originalImage.setPixelRGBA(x, y, 0);
				}
			}
		}
	}

}
