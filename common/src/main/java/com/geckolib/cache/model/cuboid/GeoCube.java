package com.geckolib.cache.model.cuboid;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/// Baked cuboid for a [GeoBone]
///
/// @param quads The quad array for this cube, pre-sorted to render in correct order
/// @param pivot The pivot point of this cube
/// @param rotation The baked rotation value of this cube
/// @param size The x/y/z dimensions of this cube
public record GeoCube(@Nullable GeoQuad[] quads, Vec3 pivot, Vec3 rotation, Vec3 size) {
    /// Submit this cuboid's quads to the vertex consumer
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {
        translateToPivotPoint(poseStack);
        rotate(poseStack);
        translateAwayFromPivotPoint(poseStack);

        final Matrix3f normalisedPoseState = poseStack.last().normal();
        final Matrix4f poseState = poseStack.last().pose();

        for (GeoQuad quad : this.quads) {
            if (quad == null)
                continue;

            Vector3f normal = normalisedPoseState.transform(quad.normalVec());

            RenderUtil.fixInvertedFlatCube(this, normal);
            quad.render(poseState, normal, vertexConsumer, packedLight, packedOverlay, renderColor);
        }

    }

    /// Apply a rotation to the provided PoseStack by this cube's rotation values
    public void rotate(PoseStack poseStack) {
        RenderUtil.optionalRotateZYX(poseStack, this.rotation.z, this.rotation.y, this.rotation.x);
    }

    /// Apply a translation to the provided PoseStack to this cube's pivot point
    public void translateToPivotPoint(PoseStack poseStack) {
        poseStack.translate(pivot().x() / 16f, pivot().y() / 16f, pivot().z() / 16f);
    }

    /// Apply a translation to the provided PoseStack away from this cube's pivot point
    public void translateAwayFromPivotPoint(PoseStack poseStack) {
        poseStack.translate(-pivot().x() / 16f, -pivot().y() / 16f, -pivot().z() / 16f);
    }
}
