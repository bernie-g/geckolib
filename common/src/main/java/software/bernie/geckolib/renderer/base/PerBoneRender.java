package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * A functional interface for performing a render operation at the {@link PoseStack.Pose pose} of a bone.
 * <p>
 * Typically you would submit an instance of this to {@link GeoRenderLayer#addPerBoneRender}
 */
@FunctionalInterface
public interface PerBoneRender<R extends GeoRenderState> {
    void render(R renderState, PoseStack poseStack, GeoBone bone, @Nullable RenderType renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, int renderColor);

    /**
     * Internal API method to run this render task
     * <p>
     * You should <b><u>NOT</u></b> be overriding this
     */
    @ApiStatus.Internal
    default void runTask(R renderState, PoseStack poseStack, GeoBone bone, PoseStack.Pose pose, @Nullable RenderType renderType, MultiBufferSource bufferSource,
                         int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();
        poseStack.last().set(pose);
        render(renderState, poseStack, bone, renderType, bufferSource, packedLight, packedOverlay, renderColor);
        poseStack.popPose();
    }
}