package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TextureMesh {
	private double[] localPivot;
	private double[] position;
	private double[] rotation;
	private double[] scale;
	private String texture;

	/**
	 * The pivot point on the texture (in *texture space* not entity or bone space)
	 * of the texture geometry
	 */
	@JsonProperty("local_pivot")
	public double[] getLocalPivot() {
		return localPivot;
	}

	@JsonProperty("local_pivot")
	public void setLocalPivot(double[] value) {
		this.localPivot = value;
	}

	/**
	 * The position of the pivot point after rotation (in *entity space* not texture
	 * or bone space) of the texture geometry
	 */
	@JsonProperty("position")
	public double[] getPosition() {
		return position;
	}

	@JsonProperty("position")
	public void setPosition(double[] value) {
		this.position = value;
	}

	/**
	 * The rotation (in degrees) of the texture geometry relative to the offset
	 */
	@JsonProperty("rotation")
	public double[] getRotation() {
		return rotation;
	}

	@JsonProperty("rotation")
	public void setRotation(double[] value) {
		this.rotation = value;
	}

	/**
	 * The scale (in degrees) of the texture geometry relative to the offset
	 */
	@JsonProperty("scale")
	public double[] getScale() {
		return scale;
	}

	@JsonProperty("scale")
	public void setScale(double[] value) {
		this.scale = value;
	}

	/**
	 * The friendly-named texture to use.
	 */
	@JsonProperty("texture")
	public String getTexture() {
		return texture;
	}

	@JsonProperty("texture")
	public void setTexture(String value) {
		this.texture = value;
	}
}
