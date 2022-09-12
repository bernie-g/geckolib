package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specifies the UV's for the face that stretches along the x and z axes, and
 * faces the -y axis
 *
 * Specifies the UV's for the face that stretches along the z and y axes, and
 * faces the x axis
 *
 * Specifies the UV's for the face that stretches along the x and y axes, and
 * faces the -z axis.
 *
 * Specifies the UV's for the face that stretches along the x and y axes, and
 * faces the z axis
 *
 * Specifies the UV's for the face that stretches along the x and z axes, and
 * faces the y axis
 *
 * Specifies the UV's for the face that stretches along the z and y axes, and
 * faces the -x axis
 */
public class FaceUv {
	private String materialInstance;
	private double[] uv;
	private double[] uvSize;

	@JsonProperty("material_instance")
	public String getMaterialInstance() {
		return materialInstance;
	}

	@JsonProperty("material_instance")
	public void setMaterialInstance(String value) {
		this.materialInstance = value;
	}

	/**
	 * Specifies the uv origin for the face. For this face, it is the upper-left
	 * corner, when looking at the face with y being up.
	 */
	@JsonProperty("uv")
	public double[] getUv() {
		return uv;
	}

	@JsonProperty("uv")
	public void setUv(double[] value) {
		this.uv = value;
	}

	/**
	 * The face maps this many texels from the uv origin. If not specified, the box
	 * dimensions are used instead.
	 */
	@JsonProperty("uv_size")
	public double[] getUvSize() {
		return uvSize;
	}

	@JsonProperty("uv_size")
	public void setUvSize(double[] value) {
		this.uvSize = value;
	}
}
