package software.bernie.geckolib.loading.json;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Geo format version enum, mostly just used in deserialization at startup
 */
public enum ModelFormatVersion {
	V_1_12_0("1.12.0"),
	V_1_14_0("1.14.0"),
	V_1_21_0("1.21.0");

	private static final Supplier<Map<String, ModelFormatVersion>> LOOKUP = Suppliers.memoize(() -> Util.make(new Object2ObjectOpenHashMap<>(), map -> {
		for (ModelFormatVersion formatVersion : values()) {
			map.put(formatVersion.serializedName, formatVersion);
			map.put(formatVersion.name(), formatVersion);
		}
	}));

	private final String serializedName;
	private final boolean supported;
	private final String errorMessage;

	ModelFormatVersion(String serializedName) {
		this(serializedName, null);
	}

	ModelFormatVersion(String serializedName, @Nullable String errorMessage) {
		this.serializedName = serializedName;
		this.supported = errorMessage == null;
		this.errorMessage = errorMessage;
	}

	public String getSerializedName() {
		return this.serializedName;
	}

	public boolean isSupported() {
		return this.supported;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public static @Nullable ModelFormatVersion match(String version) {
		return LOOKUP.get().get(version);
	}
}
