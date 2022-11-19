/*
    Direct copy of https://github.com/shedaniel/cloth-basic-math/blob/master/src/main/java/me/shedaniel/math/Color.java under the unlicense.
 */
package software.bernie.geckolib.core.object;

/**
 * Color holder object for storing a packed int argb value.
 */
public record Color(int argbInt) {
	public static final Color WHITE = new Color(0xFFFFFFFF);
	public static final Color LIGHT_GRAY = new Color(0xFFC0C0C0);
	public static final Color GRAY = new Color(0xFF808080);
	public static final Color DARK_GRAY = new Color(0xFF404040);
	public static final Color BLACK = new Color(0xFF000000);
	public static final Color RED = new Color(0xFFFF0000);
	public static final Color PINK = new Color(0xFFFFAFAF);
	public static final Color ORANGE = new Color(0xFFFFC800);
	public static final Color YELLOW = new Color(0xFFFFFF00);
	public static final Color GREEN = new Color(0xFF00FF00);
	public static final Color MAGENTA = new Color(0xFFFF00FF);
	public static final Color CYAN = new Color(0xFF00FFFF);
	public static final Color BLUE = new Color(0xFF0000FF);

	/**
	 * Creates a new {@code Color} instance from RGB values, ensuring 100% opacity
	 */
	public static Color ofOpaque(int color) {
		return new Color(0xFF000000 | color);
	}

	/**
	 * Creates a new {@code Color} instance from RGB values with 100% opacity
	 */
	public static Color ofRGB(float red, float green, float blue) {
		return ofRGBA(red, green, blue, 1f);
	}

	/**
	 * Creates a new {@code Color} instance from RGB values with 100% opacity
	 */
	public static Color ofRGB(int r, int g, int b) {
		return ofRGBA(r, g, b, 255);
	}

	/**
	 * Creates a new {@code Color} instance from RGBA values
	 */
	public static Color ofRGBA(float r, float g, float b, float a) {
		return ofRGBA((int)(r * 255f + 0.5), (int)(g * 255f + 0.5f), (int)(b * 255f + 0.5f), (int)(a * 255f + 0.5f));
	}

	/**
	 * Creates a new {@code Color} instance from RGBA values
	 */
	public static Color ofRGBA(int r, int g, int b, int a) {
		return new Color(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}

	/**
	 * Creates a new {@code Color} instance from HSB values with 100% opacity
	 */
	public static Color ofHSB(float hue, float saturation, float brightness) {
		return ofOpaque(HSBtoARGB(hue, saturation, brightness));
	}

	/**
	 * Converts a HSB value triplet to a packed ARGB int
	 */
	public static int HSBtoARGB(float hue, float saturation, float brightness) {
		int r = 0;
		int g = 0;
		int b = 0;

		if (saturation == 0) {
			r = g = b = (int) (brightness * 255f + 0.5f);
		}
		else {
			float h = (hue - (float)Math.floor(hue)) * 6f;
			float f = h - (float)Math.floor(h);
			float p = brightness * (1 - saturation);
			float q = brightness * (1 - saturation * f);
			float t = brightness * (1 - (saturation * (1 - f)));

			switch ((int)h) {
				case 0 -> {
					r = (int) (brightness * 255f + 0.5f);
					g = (int) (t * 255f + 0.5f);
					b = (int) (p * 255f + 0.5f);
				}
				case 1 -> {
					r = (int) (q * 255f + 0.5f);
					g = (int) (brightness * 255f + 0.5f);
					b = (int) (p * 255f + 0.5f);
				}
				case 2 -> {
					r = (int) (p * 255f + 0.5f);
					g = (int) (brightness * 255f + 0.5f);
					b = (int) (t * 255f + 0.5f);
				}
				case 3 -> {
					r = (int) (p * 255f + 0.5f);
					g = (int) (q * 255f + 0.5f);
					b = (int) (brightness * 255f + 0.5f);
				}
				case 4 -> {
					r = (int) (t * 255f + 0.5f);
					g = (int) (p * 255f + 0.5f);
					b = (int) (brightness * 255f + 0.5f);
				}
				case 5 -> {
					r = (int) (brightness * 255f + 0.5f);
					g = (int) (p * 255f + 0.5f);
					b = (int) (q * 255f + 0.5f);
				}
			}
		}

		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}

	public int getColor() {
		return this.argbInt;
	}

	public int getAlpha() {
		return this.argbInt >> 24 & 0xFF;
	}

	public float getAlphaFloat() {
		return getAlpha() / 255f;
	}

	public int getRed() {
		return this.argbInt >> 16 & 0xFF;
	}

	public float getRedFloat() {
		return getRed() / 255f;
	}

	public int getGreen() {
		return this.argbInt >> 8 & 0xFF;
	}

	public float getGreenFloat() {
		return getGreen() / 255f;
	}

	public int getBlue() {
		return this.argbInt & 0xFF;
	}

	public float getBlueFloat() {
		return getBlue() / 255f;
	}

	/**
	 * Returns a brighter variant of the same color.<br>
	 * @param factor The factor for shading
	 */
	public Color brighter(double factor) {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		int i = (int)(1 / (1 - (1 / factor)));

		if (r == 0 && g == 0 && b == 0)
			return ofRGBA(i, i, i, getAlpha());

		if (r > 0 && r < i)
			r = i;

		if (g > 0 && g < i)
			g = i;

		if (b > 0 && b < i)
			b = i;

		return ofRGBA(Math.min((int) (r / (1 / factor)), 255), Math.min((int) (g / (1 / factor)), 255),
				Math.min((int) (b / (1 / factor)), 255), getAlpha());
	}

	/**
	 * Returns a darker variant of the same color.<br>
	 * @param factor The factor for shading. The value provided is an inversely relative multiplier.<br>
	 *                  E.G. input=2 -> 2x as dark.<br>
	 *                  E.G. input=0.5 -> 0.5x as dark (brighter)
	 */
	public Color darker(float factor) {
		return ofRGBA(Math.max((int)(getRed() * (1 / factor)), 0), Math.max((int)(getGreen() * (1 / factor)), 0),
				Math.max((int)(getBlue() * (1 / factor)), 0), getAlpha());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (getClass() != other.getClass())
			return false;

		return hashCode() == other.hashCode();
	}

	@Override
	public int hashCode() {
		return argbInt;
	}

	@Override
	public String toString() {
		return String.valueOf(argbInt);
	}
}
