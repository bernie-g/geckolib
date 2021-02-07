package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MinecraftGeometry {
	private Bone[] bones;
	private String cape;
	private ModelProperties modelProperties;

	/**
	 * Bones define the 'skeleton' of the mob: the parts that can be animated, and
	 * to which geometry and other bones are attached.
	 */
	@JsonProperty("bones")
	public Bone[] getBones() {
		return bones;
	}

	@JsonProperty("bones")
	public void setBones(Bone[] value) {
		this.bones = value;
	}

	@JsonProperty("cape")
	public String getCape() {
		return cape;
	}

	@JsonProperty("cape")
	public void setCape(String value) {
		this.cape = value;
	}

	@JsonProperty("description")
	public ModelProperties getProperties() {
		return modelProperties;
	}

	@JsonProperty("description")
	public void setProperties(ModelProperties value) {
		this.modelProperties = value;
	}
}
