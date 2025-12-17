package software.bernie.geckolib.cache.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.util.MiscUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * Baked "bone" group object representing a collection of renderable objects (E.G. cubes) and recursively nested children.
 * <p>
 * GeoBones themselves do not render; instead they render the renderable objects they contain (such as {@link GeoCube}s).
 * <p>
 * Each bone instance is a singleton, belonging to a single {@link BakedGeoModel}
 */
public abstract class GeoBone {
	protected final @Nullable GeoBone parent;
	protected final String name;

    protected final GeoBone[] children;

    protected final float pivotX;
    protected final float pivotY;
    protected final float pivotZ;

    protected final float baseRotX;
    protected final float baseRotY;
    protected final float baseRotZ;

    @ApiStatus.Internal
    public @Nullable BoneSnapshot frameSnapshot = null;
    @ApiStatus.Internal
    public RenderPassInfo.BonePositionListener @Nullable[] positionListeners = null;

    protected GeoBone(@Nullable GeoBone parent, String name, GeoBone[] children, float pivotX, float pivotY, float pivotZ, float rotX, float rotY, float rotZ) {
        this.parent = parent;
        this.name = name;
        this.children = children;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.pivotZ = pivotZ;
        this.baseRotX = rotX;
        this.baseRotY = rotY;
        this.baseRotZ = rotZ;
    }

    /**
     * @return The parent bone of this bone, or null if this bone has no parent
     */
    public @Nullable GeoBone parent() {
        return this.parent;
    }

    /**
     * @return The name of this bone, as defined in the model json
     */
    public String name() {
        return this.name;
    }

    /**
     * @return The child bones of this bone
     */
    public GeoBone[] children() {
        return this.children;
    }

    /**
     * @return The pivot x coordinate of this bone, relative to its parent bone
     */
    public float pivotX() {
        return this.pivotX;
    }

    /**
     * @return The pivot y coordinate of this bone, relative to its parent bone
     */
    public float pivotY() {
        return this.pivotY;
    }

    /**
     * @return The pivot z coordinate of this bone, relative to its parent bone
     */
    public float pivotZ() {
        return this.pivotZ;
    }

    /**
     * @return The base x rotation of this bone in radians, relative to its parent bone
     */
    public float baseRotX() {
        return this.baseRotX;
    }

    /**
     * @return The base y rotation of this bone in radians, relative to its parent bone
     */
    public float baseRotY() {
        return this.baseRotY;
    }

    /**
     * @return The base z rotation of this bone in radians, relative to its parent bone
     */
    public float baseRotZ() {
        return this.baseRotZ;
    }

    /**
     * Render this GeoBone using the provided {@link RenderPassInfo}
     */
    public abstract <R extends GeoRenderState> void render(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor);

    /**
     * Render all {@link #children} of this bone
     */
    public void renderChildren(RenderPassInfo<?> renderPassInfo, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {
        if (this.frameSnapshot == null || !this.frameSnapshot.areChildrenHidden()) {
            for (GeoBone child : this.children) {
                poseStack.pushPose();
                RenderUtil.prepMatrixForBone(poseStack, child);
                child.updateBonePositionListeners(poseStack, renderPassInfo);

                child.render(renderPassInfo, poseStack, vertexConsumer, packedLight, packedOverlay, renderColor);
                child.renderChildren(renderPassInfo, poseStack, vertexConsumer, packedLight, packedOverlay, renderColor);

                poseStack.popPose();
            }
        }
    }

    /**
     * Apply a translation to the provided PoseStack to this bone's pivot point
     */
    public void translateToPivotPoint(PoseStack poseStack) {
        poseStack.translate(pivotX() / 16f, pivotY() / 16f, pivotZ() / 16f);
    }

    /**
     * Apply a translation to the provided PoseStack away from this bone's pivot point
     */
    public void translateAwayFromPivotPoint(PoseStack poseStack) {
        poseStack.translate(-pivotX() / 16f, -pivotY() / 16f, -pivotZ() / 16f);
    }

    /**
     * Pass the current render position to any applied {@link RenderPassInfo.BonePositionListener}s
     */
    @ApiStatus.Internal
    public void updateBonePositionListeners(PoseStack poseStack, RenderPassInfo<?> renderPassInfo) {
        if (this.positionListeners != null) {
            final Matrix4f bonePose = new Matrix4f(poseStack.last().pose());
            final Matrix4f localPose = RenderUtil.extractPoseFromRoot(bonePose, renderPassInfo.getPreRenderMatrixState());
            final Matrix4f modelPose = RenderUtil.extractPoseFromRoot(bonePose, renderPassInfo.getModelRenderMatrixState());
            final Vec3 position = renderPassInfo.renderState().getOrDefaultGeckolibData(DataTickets.POSITION, null);
            final Matrix4f worldPose = position == null ? null : new Matrix4f(modelPose).translate(position.toVector3f());
            final Vec3 localPos = RenderUtil.renderPoseToPosition(localPose, 1, 1, 1);
            final Vec3 modelPos = RenderUtil.renderPoseToPosition(modelPose, -16, 16, 16);
            final Vec3 worldPos = worldPose == null ? null : RenderUtil.renderPoseToPosition(worldPose, 1, 1, 1);

            for (int i = 0; i < this.positionListeners.length; i++) {
                this.positionListeners[i].accept(worldPos, modelPos, localPos);
            }
        }
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

        final GeoBone other = (GeoBone)obj;

        if (this.parent != other.parent)
            return false;

        if (!this.name.equals(other.name))
            return false;

        if (!MiscUtil.areFloatsEqual(this.pivotX, other.pivotX) ||
            !MiscUtil.areFloatsEqual(this.pivotY, other.pivotY) ||
            !MiscUtil.areFloatsEqual(this.pivotZ, other.pivotZ))
            return false;

		return Arrays.equals(this.children, other.children);
	}

    @Override
	public int hashCode() {
		return Objects.hash(this.parent == null ? "" : this.parent, this.name, Arrays.hashCode(this.children), this.pivotX, this.pivotY, this.pivotZ);
	}
}
