package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawGeoModel {
	private FormatVersion formatVersion;
	private MinecraftGeometry[] minecraftGeometry;

	@JsonProperty("format_version")
	public FormatVersion getFormatVersion() {
		return formatVersion;
	}

	@JsonProperty("format_version")
	public void setFormatVersion(FormatVersion value) {
		this.formatVersion = value;
	}

	@JsonProperty("minecraft:geometry")
	public MinecraftGeometry[] getMinecraftGeometry() {
		return minecraftGeometry;
	}

	@JsonProperty("minecraft:geometry")
	public void setMinecraftGeometry(MinecraftGeometry[] value) {
		this.minecraftGeometry = value;
	}
}
