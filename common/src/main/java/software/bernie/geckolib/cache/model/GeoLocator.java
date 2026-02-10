package software.bernie.geckolib.cache.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

/// Implementation of GeoBone for locator markers
///
/// These are non-rendering node elements, typically used for positioning
public class GeoLocator extends GeoBone {
    public GeoLocator(@Nullable GeoBone parent, String name, GeoBone[] children, float pivotX, float pivotY, float pivotZ, float rotX, float rotY, float rotZ) {
        super(parent, name, children, pivotX, pivotY, pivotZ, rotX, rotY, rotZ);
    }

    /// Render this GeoBone using the provided [RenderPassInfo]
    @Override
    public <R extends GeoRenderState> void render(RenderPassInfo<R> renderPassInfo, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor) {}
}
