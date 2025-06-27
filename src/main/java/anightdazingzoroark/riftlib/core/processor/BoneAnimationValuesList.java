package anightdazingzoroark.riftlib.core.processor;

import anightdazingzoroark.riftlib.molang.utils.MathHelper;

import java.util.HashMap;

public class BoneAnimationValuesList {
    private final HashMap<String, BoneAnimationValues> map = new HashMap<>();

    public void addRotations(String name, float x, float y, float z) {
        if (this.map.containsKey(name)) {
            float newX = MathHelper.wrapDegrees(this.map.get(name).rotation[0] + x);
            float newY = MathHelper.wrapDegrees(this.map.get(name).rotation[1] + y);
            float newZ = MathHelper.wrapDegrees(this.map.get(name).rotation[2] + z);
            BoneAnimationValues newValues = this.map.get(name);
            newValues.rotation = new float[]{newX, newY, newZ};
            this.map.replace(name, newValues);
        }
        else {
            BoneAnimationValues newValues = new BoneAnimationValues();
            newValues.rotation = new float[]{x, y, z};
            this.map.put(name, newValues);
        }
    }

    public float[] getRotations(String name) {
        if (this.map.containsKey(name)) return this.map.get(name).rotation;
        return new float[]{0, 0, 0};
    }

    public String getRotationsAsString(String name) {
        return "rotations: ("+Math.toDegrees(this.getRotations(name)[0])+", "+Math.toDegrees(this.getRotations(name)[1])+", "+Math.toDegrees(this.getRotations(name)[2])+")";
    }

    public void addPositions(String name, float x, float y, float z) {
        if (this.map.containsKey(name)) {
            float newX = this.map.get(name).position[0] + x;
            float newY = this.map.get(name).position[1] + y;
            float newZ = this.map.get(name).position[2] + z;
            BoneAnimationValues newValues = this.map.get(name);
            newValues.position = new float[]{newX, newY, newZ};
            this.map.replace(name, newValues);
        }
        else {
            BoneAnimationValues newValues = new BoneAnimationValues();
            newValues.position = new float[]{x, y, z};
            this.map.put(name, newValues);
        }
    }

    public float[] getPositions(String name) {
        if (this.map.containsKey(name)) return this.map.get(name).position;
        return new float[]{0, 0, 0};
    }

    public void addScales(String name, float x, float y, float z) {
        if (this.map.containsKey(name)) {
            float newX = this.map.get(name).scale[0] + (x - 1f);
            float newY = this.map.get(name).scale[1] + (y - 1f);
            float newZ = this.map.get(name).scale[2] + (z - 1f);
            BoneAnimationValues newValues = this.map.get(name);
            newValues.scale = new float[]{newX, newY, newZ};
            this.map.replace(name, newValues);
        }
        else {
            BoneAnimationValues newValues = new BoneAnimationValues();
            newValues.scale = new float[]{x, y, z};
            this.map.put(name, newValues);
        }
    }

    public float[] getScales(String name) {
        if (this.map.containsKey(name)) return this.map.get(name).scale;
        return new float[]{1f, 1f, 1f};
    }

    private class BoneAnimationValues {
        protected float[] rotation = {0f, 0f, 0f};
        protected float[] position = {0f, 0f, 0f};
        protected float[] scale = {1f, 1f, 1f};
    }
}
