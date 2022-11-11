package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocatorClass {
	private Boolean ignoreInheritedScale;
	private double[] offset;
	private double[] rotation;

	/**
	 * Discard scale inherited from parent bone.
	 */
	@JsonProperty("ignore_inherited_scale")
	public Boolean getIgnoreInheritedScale() {
		return ignoreInheritedScale;
	}

	@JsonProperty("ignore_inherited_scale")
	public void setIgnoreInheritedScale(Boolean value) {
		this.ignoreInheritedScale = value;
	}

	/**
	 * Position of the locator in model space.
	 */
	@JsonProperty("offset")
	public double[] getOffset() {
		return offset;
	}

	@JsonProperty("offset")
	public void setOffset(double[] value) {
		this.offset = value;
	}

	/**
	 * Rotation of the locator in model space.
	 */
	@JsonProperty("rotation")
	public double[] getRotation() {
		return rotation;
	}

	@JsonProperty("rotation")
	public void setRotation(double[] value) {
		this.rotation = value;
	}
}
