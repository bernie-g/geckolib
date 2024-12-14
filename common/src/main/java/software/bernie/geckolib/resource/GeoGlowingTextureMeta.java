package software.bernie.geckolib.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ARGB;

import java.util.List;
import java.util.function.Function;

/**
 * Metadata class that stores the data for GeckoLib's {@link software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer emissive texture feature} for a given texture
 */
public record GeoGlowingTextureMeta(List<Pixel> pixels) {
	public static final Codec<GeoGlowingTextureMeta> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Pixel.FROM_REGION_CODEC
					.listOf()
					.comapFlatMap(pixelsLists -> {
						ImmutableList.Builder<Pixel> pixelsBuilder = ImmutableList.builder();

						pixelsLists.forEach(pixelsBuilder::addAll);

						return DataResult.success((List<Pixel>)pixelsBuilder.build());
					}, List::of)
					.fieldOf("sections").forGetter(GeoGlowingTextureMeta::pixels)
	).apply(builder, GeoGlowingTextureMeta::new));
	public static final MetadataSectionType<GeoGlowingTextureMeta> TYPE = new MetadataSectionType<>("glowsections", CODEC);

	/**
	 * Generate the GlowLayer pixels list from an existing image resource, instead of using the .png.mcmeta file
	 */
	public static GeoGlowingTextureMeta fromExistingImage(NativeImage glowLayer) {
		List<Pixel> pixels = new ObjectArrayList<>();

		for (int x = 0; x < glowLayer.getWidth(); x++) {
			for (int y = 0; y < glowLayer.getHeight(); y++) {
				int color = glowLayer.getPixel(x, y);

				if (color != 0)
					pixels.add(new Pixel(x, y, ARGB.alpha(color)));
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
			int color = originalImage.getPixel(pixel.x, pixel.y);

			if (pixel.alpha > 0)
				color = ARGB.color(pixel.alpha, ARGB.red(color), ARGB.green(color), ARGB.blue(color));

			newImage.setPixel(pixel.x, pixel.y, color);
			originalImage.setPixel(pixel.x, pixel.y, 0);
		}
	}

	/**
	 * A pixel marker for a glowlayer mask
	 *
	 * @param x The X coordinate of the pixel
	 * @param y The Y coordinate of the pixel
	 * @param alpha The alpha value of the mask
	 */
	private record Pixel(int x, int y, int alpha) {
		public static final Codec<Pixel> SINGLE_CODEC = RecordCodecBuilder.create(builder -> builder.group(
				Codec.INT.fieldOf("x").forGetter(Pixel::x),
				Codec.INT.fieldOf("y").forGetter(Pixel::y),
				Codec.INT.fieldOf("alpha").forGetter(Pixel::alpha)
		).apply(builder, Pixel::new));
		public static final Codec<List<Pixel>> FROM_REGION_CODEC = Region.CODEC
				.flatComapMap(Region::toPixels, Region::fromPixels)
				.validate(list -> list.isEmpty() ? DataResult.error(() -> "Empty region! Pixel region must have at least one pixel!") : DataResult.success(list));

		private record Region(int xMin, int yMin, Either<Integer, Integer> x2, Either<Integer, Integer> y2, int alpha) {
			public static final Codec<Region> CODEC = RecordCodecBuilder.create(builder -> builder.group(
					Codec.mapEither(Codec.INT.fieldOf("x1"), Codec.INT.optionalFieldOf("x", 0))
							.xmap(either -> either.map(Function.identity(), Function.identity()), Either::right).forGetter(Region::xMin),
					Codec.mapEither(Codec.INT.fieldOf("y1"), Codec.INT.optionalFieldOf("y", 0))
							.xmap(either -> either.map(Function.identity(), Function.identity()), Either::right).forGetter(Region::yMin),
					Codec.mapEither(Codec.INT.fieldOf("x2"), Codec.INT.optionalFieldOf("w", 0)).forGetter(Region::x2),
					Codec.mapEither(Codec.INT.fieldOf("y2"), Codec.INT.optionalFieldOf("h", 0)).forGetter(Region::y2),
					Codec.INT.optionalFieldOf("alpha", 255).forGetter(Region::alpha)
			).apply(builder, Region::new));

			private List<Pixel> toPixels() {
				int xMax = this.x2.map(Function.identity(), w -> w + this.xMin);
				int yMax = this.y2.map(Function.identity(), h -> h + this.yMin);
				List<Pixel> pixels = new ObjectArrayList<>();

				for (int x = this.xMin; x <= xMax; x++) {
					for (int y = this.yMin; y <= yMax; y++) {
						pixels.add(new Pixel(x, y, this.alpha));
					}
				}

				return pixels;
			}

			private static DataResult<Region> fromPixels(List<Pixel> pixels) {
				if (pixels.isEmpty())
					return DataResult.error(() -> "Pixel region must not be empty!");

				int minX = 0;
				int minY = 0;
				int maxX = 0;
				int maxY = 0;
				int alpha = pixels.getFirst().alpha;

				for (Pixel pixel : pixels) {
					minX = Math.min(minX, pixel.x);
					minY = Math.min(minY, pixel.y);
					maxX = Math.max(maxX, pixel.x);
					maxY = Math.max(maxY, pixel.y);

					if (pixel.alpha != alpha)
						return DataResult.error(() -> "Pixel in region has mismatching alpha value! All pixels in a region must have the same alpha value");
				}

				if ((maxX - minX) * (maxY - minY) != pixels.size())
					return DataResult.error(() -> "Invalid pixel region defined. Pixel regions must be contiguous square or rectangular sections");

				return DataResult.success(new Region(minX, minY, Either.left(maxX), Either.left(maxY), alpha));
			}
		}
	}
}
