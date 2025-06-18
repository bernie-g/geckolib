package anightdazingzoroark.riftlib.geo.render.built;

public class GeoLocator {
    public GeoBone parent;

    public final String name;

    public final float positionX;
    public final float positionY;
    public final float positionZ;

    public GeoLocator(String name, float x, float y, float z) {
        this.name = name;
        this.positionX = x;
        this.positionY = y;
        this.positionZ = z;
    }
}
