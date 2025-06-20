package anightdazingzoroark.riftlib.geo.render.built;

import net.minecraft.util.math.Vec3d;

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
    public Vec3d getOffsetFromRotations() {
        Vec3d toReturn = new Vec3d(this.positionX, this.positionY, this.positionZ);
        GeoBone boneToTest = this.parent;

        while (boneToTest != null) {
            double pivotX = boneToTest.getPivotX() / 16.0;
            double pivotY = boneToTest.getPivotY() / 16.0;
            double pivotZ = boneToTest.getPivotZ() / 16.0;

            double relX = toReturn.x - pivotX;
            double relY = toReturn.y - pivotY;
            double relZ = toReturn.z - pivotZ;

            //create offsets from x rotation, which affects y and z offsets
            double cosX = Math.cos(boneToTest.getRotationX());
            double sinX = Math.sin(boneToTest.getRotationX());
            double ry = relY * cosX - relZ * sinX;
            double rz = relY * sinX + relZ * cosX;
            relY = ry;
            relZ = rz;

            //create offsets from y rotation, which affects x and z offsets
            double cosY = Math.cos(boneToTest.getRotationY());
            double sinY = Math.sin(boneToTest.getRotationY());
            double rx = relX * cosY - relZ * sinY;
            rz = relX * sinY + relZ * cosY;
            relX = rx;
            relZ = rz;

            //create offsets from z rotation, which affects x and y offsets
            double cosZ = Math.cos(boneToTest.getRotationZ());
            double sinZ = Math.sin(boneToTest.getRotationZ());
            rx = relX * cosZ - relY * sinZ;
            ry = relX * sinZ + relY * cosZ;
            relX = rx;
            relY = ry;

            toReturn = new Vec3d(relX + pivotX, relY + pivotY, relZ + pivotZ);
            boneToTest = boneToTest.parent;
        }

        return toReturn.subtract(this.positionX, this.positionY, this.positionZ);
    }

    public String toString() {
        return "[name="+this.name+", position=("+this.positionX+", "+this.positionY+", "+this.positionZ+")]";
    }
}
