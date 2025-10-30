package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * A functional interface for submitting a render operation at the {@link PoseStack.Pose pose} of a bone.
 * <p>
 * Typically you would submit an instance of this to {@link GeoRenderLayer#addPerBoneRender}
 */
@FunctionalInterface
public interface PerBoneRender<R extends GeoRenderState> {
    /**
     * Submit your per-bone render task for this render render pass
     *
     * @param renderState The RenderState for this render pass
     * @param poseStack The PoseStack for this render pass. It has already been positioned at the bone's position
     * @param bone The GeoBone to submit the task for
     * @param renderTasks The render submission collector for this render pass. Submit your render task to this
     * @param cameraState The state of the camera for this render pass
     * @param packedLight The packed light level for this render pass
     * @param packedOverlay The packed overlay level for this render pass
     * @param renderColor The color to render with for this render pass
     */
    void submitRenderTask(R renderState, PoseStack poseStack, GeoBone bone, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor);
}