package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * A functional interface for performing a render operation at the {@link PoseStack.Pose pose} of a bone.
 * <p>
 * Typically you would submit an instance of this to {@link GeoRenderLayer#addPerBoneRender}
 */
@FunctionalInterface
public interface PerBoneRender<R extends GeoRenderState> {
    void submitRenderTask(R renderState, PoseStack poseStack, GeoBone bone, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor);

    /**
     * Internal API method to run this render task
     * <p>
     * You should <b><u>NOT</u></b> be overriding this
     */
    @ApiStatus.Internal
    default void runTask(R renderState, PoseStack poseStack, GeoBone bone, PoseStack.Pose pose, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                         int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();
        poseStack.last().set(pose);
        submitRenderTask(renderState, poseStack, bone, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
        poseStack.popPose();
    }
}