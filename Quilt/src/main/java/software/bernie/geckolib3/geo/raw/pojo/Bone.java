package software.bernie.geckolib3.geo.raw.pojo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bone {
	private double[] bindPoseRotation;
	private Cube[] cubes;
	private Boolean debug;
	private Double inflate;
	private Map<String, LocatorValue> locators;
	private Boolean mirror;
	private String name;
	private Boolean neverRender;
	private String parent;
	private double[] pivot = new double[] { 0, 0, 0 };
	private PolyMesh polyMesh;
	private Long renderGroupID;
	private Boolean reset;
	private double[] rotation = new double[] { 0, 0, 0 };
	private TextureMesh[] textureMeshes;

	@JsonProperty("bind_pose_rotation")
	public double[] getBindPoseRotation() {
		return bindPoseRotation;
	}

	@JsonProperty("bind_pose_rotation")
	public void setBindPoseRotation(double[] value) {
		this.bindPoseRotation = value;
	}

	/**
	 * This is the list of cubes associated with this bone.
	 */
	@JsonProperty("cubes")
	public Cube[] getCubes() {
		return cubes;
	}

	@JsonProperty("cubes")
	public void setCubes(Cube[] value) {
		this.cubes = value;
	}

	@JsonProperty("debug")
	public Boolean getDebug() {
		return debug;
	}

	@JsonProperty("debug")
	public void setDebug(Boolean value) {
		this.debug = value;
	}

	/**
	 * Grow this box by this additive amount in all directions (in model space
	 * units).
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
	 * This is a list of locators associated with this bone. A locator is a point in
	 * model space that tracks a particular bone as the bone animates (by
	 * maintaining it's relationship to the bone through the animation).
	 */
	@JsonProperty("locators")
	public Map<String, LocatorValue> getLocators() {
		return locators;
	}

	@JsonProperty("locators")
	public void setLocators(Map<String, LocatorValue> value) {
		this.locators = value;
	}

	/**
	 * Mirrors the UV's of the unrotated cubes along the x axis, also causes the
	 * east/west faces to get flipped.
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
	 * Animation files refer to this bone via this identifier.
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String value) {
		this.name = value;
	}

	@JsonProperty("neverRender")
	public Boolean getNeverRender() {
		return neverRender;
	}

	@JsonProperty("neverRender")
	public void setNeverRender(Boolean value) {
		this.neverRender = value;
	}

	/**
	 * Bone that this bone is relative to. If the parent bone moves, this bone will
	 * move along with it.
	 */
	@JsonProperty("parent")
	public String getParent() {
		return parent;
	}

	@JsonProperty("parent")
	public void setParent(String value) {
		this.parent = value;
	}

	/**
	 * The bone pivots around this point (in model space units).
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
	 * ***EXPERIMENTAL*** A triangle or quad mesh object. Can be used in conjunction
	 * with cubes and texture geometry.
	 */
	@JsonProperty("poly_mesh")
	public PolyMesh getPolyMesh() {
		return polyMesh;
	}

	@JsonProperty("poly_mesh")
	public void setPolyMesh(PolyMesh value) {
		this.polyMesh = value;
	}

	@JsonProperty("render_group_id")
	public Long getRenderGroupID() {
		return renderGroupID;
	}

	@JsonProperty("render_group_id")
	public void setRenderGroupID(Long value) {
		this.renderGroupID = value;
	}

	@JsonProperty("reset")
	public Boolean getReset() {
		return reset;
	}

	@JsonProperty("reset")
	public void setReset(Boolean value) {
		this.reset = value;
	}

	/**
	 * This is the initial rotation of the bone around the pivot, pre-animation (in
	 * degrees, x-then-y-then-z order).
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
	 * ***EXPERIMENTAL*** Adds a mesh to the bone's geometry by converting texels in
	 * a texture into boxes.
	 */
	@JsonProperty("texture_meshes")
	public TextureMesh[] getTextureMeshes() {
		return textureMeshes;
	}

	@JsonProperty("texture_meshes")
	public void setTextureMeshes(TextureMesh[] value) {
		this.textureMeshes = value;
	}
}
