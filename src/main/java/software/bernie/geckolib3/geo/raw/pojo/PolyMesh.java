package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ***EXPERIMENTAL*** A triangle or quad mesh object. Can be used in conjunction
 * with cubes and texture geometry.
 */
public class PolyMesh {
	private Boolean normalizedUvs;
	private double[] normals;
	private PolysUnion polys;
	private double[] positions;
	private double[] uvs;

	/**
	 * If true, UVs are assumed to be [0-1]. If false, UVs are assumed to be
	 * [0-texture_width] and [0-texture_height] respectively.
	 */
	@JsonProperty("normalized_uvs")
	public Boolean getNormalizedUvs() {
		return normalizedUvs;
	}

	@JsonProperty("normalized_uvs")
	public void setNormalizedUvs(Boolean value) {
		this.normalizedUvs = value;
	}

	/**
	 * Vertex normals. Can be either indexed via the "polys" section, or be a
	 * quad-list if mapped 1-to-1 to the positions and UVs sections.
	 */
	@JsonProperty("normals")
	public double[] getNormals() {
		return normals;
	}

	@JsonProperty("normals")
	public void setNormals(double[] value) {
		this.normals = value;
	}

	@JsonProperty("polys")
	public PolysUnion getPolys() {
		return polys;
	}

	@JsonProperty("polys")
	public void setPolys(PolysUnion value) {
		this.polys = value;
	}

	/**
	 * Vertex positions for the mesh. Can be either indexed via the "polys" section,
	 * or be a quad-list if mapped 1-to-1 to the normals and UVs sections.
	 */
	@JsonProperty("positions")
	public double[] getPositions() {
		return positions;
	}

	@JsonProperty("positions")
	public void setPositions(double[] value) {
		this.positions = value;
	}

	/**
	 * Vertex UVs. Can be either indexed via the "polys" section, or be a quad-list
	 * if mapped 1-to-1 to the positions and normals sections.
	 */
	@JsonProperty("uvs")
	public double[] getUvs() {
		return uvs;
	}

	@JsonProperty("uvs")
	public void setUvs(double[] value) {
		this.uvs = value;
	}
}
