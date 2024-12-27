package software.bernie.geckolib.loading.json;

import com.google.gson.annotations.SerializedName;

/**
 * Geo format version enum, mostly just used in deserialization at startup
 */
public enum FormatVersion {
	@SerializedName("1.12.0") V_1_12_0,
	@SerializedName("1.14.0") V_1_14_0,
	@SerializedName("1.21.0") V_1_21_0,
	@SerializedName("1.21.2") V_1_21_2
}
