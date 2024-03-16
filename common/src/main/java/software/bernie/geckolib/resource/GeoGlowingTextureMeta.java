package software.bernie.geckolib.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.FastColor;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Metadata class that stores the data for GeckoLib's {@link software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer emissive texture feature} for a given texture
 */
public class GeoGlowingTextureMeta {
	public static final MetadataSectionSerializer<GeoGlowingTextureMeta> DESERIALIZER = new MetadataSectionSerializer<>() {
		@Override
		public String getMetadataSectionName() {
			return "glowsections";
		}

		@Override
		public GeoGlowingTextureMeta fromJson(JsonObject json) {
			List<Pixel> pixels = fromSections(GsonHelper.getAsJsonArray(json, "sections", null));

			if (pixels.isEmpty())
				throw new JsonParseException("Empty glowlayer sections file. Must have at least one glow section!");

			return new GeoGlowingTextureMeta(pixels);
		}

		/**
		 * Generate a {@link Pixel} collection from the "sections" array of the mcmeta file
		 */
		private List<Pixel> fromSections(@Nullable JsonArray sectionsArray) {
			if (sectionsArray == null)
				return List.of();

			List<Pixel> pixels = new ObjectArrayList<>();

			for (JsonElement element : sectionsArray) {
				if (!(element instanceof JsonObject obj))
					throw new JsonParseException("Invalid glowsections json format, expected a JsonObject, found: " + element.getClass());

				int x1 = GsonHelper.getAsInt(obj, "x1", GsonHelper.getAsInt(obj, "x", 0));
				int y1 = GsonHelper.getAsInt(obj, "y1", GsonHelper.getAsInt(obj, "y", 0));
				int x2 = GsonHelper.getAsInt(obj, "x2", GsonHelper.getAsInt(obj, "w", 0) + x1);
				int y2 = GsonHelper.getAsInt(obj, "y2", GsonHelper.getAsInt(obj, "h", 0) + y1);
				int alpha = GsonHelper.getAsInt(obj, "alpha", GsonHelper.getAsInt(obj, "a", 0));

				if (x1 + y1 + x2 + y2 == 0)
					throw new IllegalArgumentException("Invalid glowsections section object, section must be at least one pixel in size");

				for (int x = x1; x <= x2; x++) {
					for (int y = y1; y <= y2; y++) {
						pixels.add(new Pixel(x, y, alpha));
					}
				}
			}

			return pixels;
		}
	};

	private final List<Pixel> pixels;

	public GeoGlowingTextureMeta(List<Pixel> pixels) {
		this.pixels = pixels;
	}

	/**
	 * Generate the GlowLayer pixels list from an existing image resource, instead of using the .png.mcmeta file
	 */
	public static GeoGlowingTextureMeta fromExistingImage(NativeImage glowLayer) {
		List<Pixel> pixels = new ObjectArrayList<>();

		for (int x = 0; x < glowLayer.getWidth(); x++) {
			for (int y = 0; y < glowLayer.getHeight(); y++) {
				int color = glowLayer.getPixelRGBA(x, y); // Actually ABGR. Blame Mojang.

				if (color != 0)
					pixels.add(new Pixel(x, y, FastColor.ABGR32.alpha(color)));
			}
		}

		if (pixels.isEmpty())
			throw new IllegalStateException("Invalid glow layer texture provided, must have at least one pixel!");

		return new GeoGlowingTextureMeta(pixels);
	}

	/**
	 * Create a new mask image based on the pre-determined pixel data
	 */
	public void createImageMask(NativeImage originalImage, NativeImage newImage) {
		for (Pixel pixel : this.pixels) {
			int color = originalImage.getPixelRGBA(pixel.x, pixel.y); // Actually ABGR. Blame Mojang.

			if (pixel.alpha > 0)
				color = FastColor.ABGR32.color(pixel.alpha, FastColor.ABGR32.blue(color), FastColor.ABGR32.green(color), FastColor.ABGR32.red(color));

			newImage.setPixelRGBA(pixel.x, pixel.y, color);
			originalImage.setPixelRGBA(pixel.x, pixel.y, 0);
		}
	}

	/**
	 * A pixel marker for a glowlayer mask
	 * @param x The X coordinate of the pixel
	 * @param y The Y coordinate of the pixel
	 * @param alpha The alpha value of the mask
	 */
	private record Pixel(int x, int y, int alpha) {}
}
