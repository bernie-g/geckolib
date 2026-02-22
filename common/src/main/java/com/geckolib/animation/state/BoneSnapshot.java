package com.geckolib.animation.state;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.ApiStatus;
import com.geckolib.cache.model.GeoBone;

/// A state container for a single [GeoBone]'s animation transformations.
///
/// This is typically computed immediately prior to rendering the associated model
/// and should be considered invalid if accessed at any time other than that immediate render frame.
public class BoneSnapshot {
	private final GeoBone bone;

	private float scaleX = 1;
	private float scaleY = 1;
	private float scaleZ = 1;

	private float translateX;
	private float translateY;
	private float translateZ;

	private float rotX;
	private float rotY;
	private float rotZ;

    private boolean skipRender = false;
    private boolean skipChildrenRender = false;

	protected BoneSnapshot(GeoBone bone) {
        this.bone = bone;
	}

    /// Create a new BoneSnapshot for the given bone, with all values at default
    public static BoneSnapshot create(GeoBone bone) {
        return new BoneSnapshot(bone);
    }

    /// @return The GeoBone associated with this snapshot
    public GeoBone getBone() {
        return this.bone;
    }

    /// @return The current x-axis scale value
    public float getScaleX() {
        return this.scaleX;
    }

    /// @return The current y-axis scale value
    public float getScaleY() {
        return this.scaleY;
    }

    /// @return The current z-axis scale value
    public float getScaleZ() {
        return this.scaleZ;
    }

    /// @return The current x-axis translation value
    public float getTranslateX() {
        return this.translateX;
    }

    /// @return The current y-axis translation value
    public float getTranslateY() {
        return this.translateY;
    }

    /// @return The current z-axis translation value
    public float getTranslateZ() {
        return this.translateZ;
    }

    /// @return The current x-axis rotation value
    public float getRotX() {
        return this.rotX;
    }

    /// @return The current y-axis rotation value
    public float getRotY() {
        return this.rotY;
    }

    /// @return The current z-axis rotation value
    public float getRotZ() {
        return this.rotZ;
    }

    /// @return Whether this bone should skip rendering itself
    /// @see #areChildrenHidden()
    public boolean isHidden() {
        return this.skipRender;
    }

    /// @return Whether this bone's children should skip rendering
    public boolean areChildrenHidden() {
        return this.skipChildrenRender;
    }

    /// @return Whether there are any translation values to use
    public boolean hasTranslation() {
        return this.translateX != 0 || this.translateY != 0 || this.translateZ != 0;
    }

    /// @return Whether there are any rotation values to use
    public boolean hasRotation() {
        return this.rotX != 0 || this.rotY != 0 || this.rotZ != 0;
    }

    /// @return Whether there are any scale values to use
    public boolean hasScale() {
        return this.scaleX != 1 || this.scaleY != 1 || this.scaleZ != 1;
    }

    /// Set the x/y/z scale values
    public BoneSnapshot setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;

        return this;
    }

    /// Set the x-axis scale value
    public BoneSnapshot setScaleX(float value) {
        this.scaleX = value;

        return this;
    }

    /// Set the y-axis scale value
    public BoneSnapshot setScaleY(float value) {
        this.scaleY = value;

        return this;
    }

    /// Set the z-axis scale value
    public BoneSnapshot setScaleZ(float value) {
        this.scaleZ = value;

        return this;
    }

    /// Set the x/y/z translation values
    public BoneSnapshot setTranslation(float x, float y, float z) {
        this.translateX = x;
        this.translateY = y;
        this.translateZ = z;

        return this;
    }

    /// Set the x-axis translation value
    public BoneSnapshot setTranslateX(float value) {
        this.translateX = value;

        return this;
    }

    /// Set the y-axis translation value
    public BoneSnapshot setTranslateY(float value) {
        this.translateY = value;

        return this;
    }

    /// Set the z-axis translation value
    public BoneSnapshot setTranslateZ(float value) {
        this.translateZ = value;

        return this;
    }

    /// Set the x/y/z rotation values
    /// Values are in radians
    public BoneSnapshot setRotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;

        return this;
    }

    /// Set the x-axis rotation value
    /// Value is in radians
    public BoneSnapshot setRotX(float value) {
        this.rotX = value;

        return this;
    }

    /// Set the y-axis rotation value
    ///      * Value is in radians
    public BoneSnapshot setRotY(float value) {
        this.rotY = value;

        return this;
    }

    /// Set the z-axis rotation value
    ///      * Value is in radians
    public BoneSnapshot setRotZ(float value) {
        this.rotZ = value;

        return this;
    }

    /// Set whether this bone should skip rendering itself
    ///
    /// NOTE: This bone's children will still render unless [#skipChildrenRender(boolean)] is set to true
    public BoneSnapshot skipRender(boolean shouldSkip) {
        this.skipRender = shouldSkip;

        return this;
    }

    /// Set whether this bone's children should skip rendering
    public BoneSnapshot skipChildrenRender(boolean shouldSkip) {
        this.skipChildrenRender = shouldSkip;

        return this;
    }

    /// Apply a scale to the provided PoseStack to scale based on this snapshot's scale values
    public void scale(PoseStack poseStack) {
        if (hasScale())
            poseStack.scale(getScaleX(), getScaleY(), getScaleZ());
    }

    /// Apply a rotation to the provided PoseStack to rotate around this snapshot's pivot point
    public void rotate(PoseStack poseStack) {
        if (getRotZ() != 0)
            poseStack.mulPose(Axis.ZP.rotation(getRotZ()));

        if (getRotY() != 0)
            poseStack.mulPose(Axis.YP.rotation(getRotY()));

        if (getRotX() != 0)
            poseStack.mulPose(Axis.XP.rotation(getRotX()));
    }

    /// Apply a translation to the provided PoseStack by this snapshot's position offset
    public void translate(PoseStack poseStack) {
        if (hasTranslation())
            poseStack.translate(-getTranslateX() / 16f, getTranslateY() / 16f, getTranslateZ() / 16f);
    }

    @ApiStatus.Internal
    public void apply() {
        this.bone.frameSnapshot = this;
    }

    @ApiStatus.Internal
    public void cleanup() {
        this.bone.frameSnapshot = null;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		return this.bone.name().equals(((BoneSnapshot)obj).bone.name());
	}

	@Override
	public int hashCode() {
		return this.bone.name().hashCode();
	}
}
