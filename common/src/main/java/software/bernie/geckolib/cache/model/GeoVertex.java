package software.bernie.geckolib.cache.model;

/// Vertex data holder
///
/// @param posX The x-coordinate position of the vertex
/// @param posY The y-coordinate position of the vertex
/// @param posZ The z-coordinate position of the vertex
/// @param texU The texture U coordinate
/// @param texV The texture V coordinate
public record GeoVertex(float posX, float posY, float posZ, float texU, float texV) {
	public GeoVertex(double x, double y, double z) {
		this((float)x, (float)y, (float)z, 0, 0);
	}

    /// Create a copy of this vertex with new UV coordinates
    public GeoVertex withUVs(double texU, double texV) {
		if (texU == this.texU && texV == this.texV)
			return this;

		return new GeoVertex(this.posX, this.posY, this.posZ, (float)texU, (float)texV);
	}
}