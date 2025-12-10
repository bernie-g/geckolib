package software.bernie.geckolib.cache.model.cuboid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.internal.RenderPassInfo;

/**
 * Implementation of GeoBone for cuboid rendering
 */
public final class CuboidGeoBone extends GeoBone {
    public final GeoCube[] cubes;

    public CuboidGeoBone(@Nullable GeoBone parent, String name, GeoBone[] children, GeoCube[] cubes, float pivotX, float pivotY, float pivotZ, float rotX, float rotY, float rotZ) {
        super(parent, name, children, pivotX, pivotY, pivotZ, rotX, rotY, rotZ);

        this.cubes = cubes;
    }

    @Override
    public <R extends GeoRenderState> void render(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {
        if (this.frameSnapshot == null || !this.frameSnapshot.isHidden()) {
            for (GeoCube cube : this.cubes) {
                poseStack.pushPose();
                cube.render(poseStack, vertexConsumer, packedLight, packedOverlay, renderColor);
                poseStack.popPose();
            }
        }
    }
}
