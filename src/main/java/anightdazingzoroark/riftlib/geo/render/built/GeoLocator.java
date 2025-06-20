package anightdazingzoroark.riftlib.geo.render.built;

public class GeoLocator {
    public final GeoBone parent;

    public final String name;

    public float positionX;
    public float positionY;
    public float positionZ;

    public GeoLocator(GeoBone parent, String name, float x, float y, float z) {
        this.parent = parent;
        this.name = name;
        this.positionX = x;
        this.positionY = y;
        this.positionZ = z;
    }

    //setting x rotation of a cube sets the y and z positions of the locators from the pivot point
    public float getXRotationFromPivot() {
        double yDist = this.positionY - this.parent.getPivotY() / 16;
        double zDist = this.positionZ - this.parent.getPivotZ() / 16;
        return (float) Math.atan2(zDist, yDist);
    }

    public String toString() {
        return "[name="+this.name+", position=("+this.positionX+", "+this.positionY+", "+this.positionZ+")]";
    }
}
