package software.bernie.geckolib.loading.definition.geometry.object;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

/// Geo format version enum, mostly just used in deserialization at startup
public enum ModelFormatVersion {
	V_1_12_0("1.12.0"),
	V_1_14_0("1.14.0"),
	V_1_16_0("1.16.0"),
	V_1_19_30("1.19.30"),
	V_1_21_0("1.21.0");

	private static final Supplier<Map<String, ModelFormatVersion>> LOOKUP = Suppliers.memoize(() -> Util.make(new Object2ObjectOpenHashMap<>(), map -> {
		for (ModelFormatVersion formatVersion : values()) {
			map.put(formatVersion.serializedName, formatVersion);
			map.put(formatVersion.name(), formatVersion);
		}
	}));

	private final String serializedName;
	private final boolean supported;
	private final @Nullable String errorMessage;

	ModelFormatVersion(String serializedName) {
		this(serializedName, null);
	}

	ModelFormatVersion(String serializedName, @Nullable String errorMessage) {
		this.serializedName = serializedName;
		this.supported = errorMessage == null;
		this.errorMessage = errorMessage;
	}

	/// The version string this version is typically represented as in geometry JSON files
	public String getSerializedName() {
		return this.serializedName;
	}

	/// @return true if the model format version is supported
	public boolean isSupported() {
		return this.supported;
	}

	/// @return The error message for incompatibility for this version, or null if it's compatible
	public @Nullable String getErrorMessage() {
		return this.errorMessage;
	}

	/// Get a `ModelFormatVersion` enum value from a string value
	///
	/// @return The matching `ModelFormatVersion`, or null if no match exists
	public static @Nullable ModelFormatVersion match(String version) {
		return LOOKUP.get().get(version);
	}
}
