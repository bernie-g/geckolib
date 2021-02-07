package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cube {
	private Double inflate;
	private Boolean mirror;
	private double[] origin = new double[] { 0, 0, 0 };
	private double[] pivot = new double[] { 0, 0, 0 };
	private double[] rotation = new double[] { 0, 0, 0 };
	private double[] size = new double[] { 1, 1, 1 };
	private UvUnion uv;

	/**
	 * Grow this box by this additive amount in all directions (in model space
	 * units), this field overrides the bone's inflate field for this cube only.
	 */
	@JsonProperty("inflate")
	public Double getInflate() {
		return inflate;
	}

	@JsonProperty("inflate")
	public void setInflate(Double value) {
		this.inflate = value;
	}

	/**
	 * Mirrors this cube about the unrotated x axis (effectively flipping the east /
	 * west faces), overriding the bone's 'mirror' setting for this cube.
	 */
	@JsonProperty("mirror")
	public Boolean getMirror() {
		return mirror;
	}

	@JsonProperty("mirror")
	public void setMirror(Boolean value) {
		this.mirror = value;
	}

	/**
	 * This point declares the unrotated lower corner of cube (smallest x/y/z value
	 * in model space units).
	 */
	@JsonProperty("origin")
	public double[] getOrigin() {
		return origin;
	}

	@JsonProperty("origin")
	public void setOrigin(double[] value) {
		this.origin = value;
	}

	/**
	 * If this field is specified, rotation of this cube occurs around this point,
	 * otherwise its rotation is around the center of the box.
	 */
	@JsonProperty("pivot")
	public double[] getPivot() {
		return pivot;
	}

	@JsonProperty("pivot")
	public void setPivot(double[] value) {
		this.pivot = value;
	}

	/**
	 * The cube is rotated by this amount (in degrees, x-then-y-then-z order) around
	 * the pivot.
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
	 * The cube extends this amount relative to its origin (in model space units).
	 */
	@JsonProperty("size")
	public double[] getSize() {
		return size;
	}

	@JsonProperty("size")
	public void setSize(double[] value) {
		this.size = value;
	}

	@JsonProperty("uv")
	public UvUnion getUv() {
		return uv;
	}

	@JsonProperty("uv")
	public void setUv(UvUnion value) {
		this.uv = value;
	}
}
