package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtil;

/**
 * Actual render handler for rendering {@link GeoBone}s
 * <p>
 * This allows separation of the {@link GeoRenderer} from the actual rendering logic
 */
public interface GeoModelRenderer<R extends GeoRenderState> {
    /**
     * Renders the provided {@link GeoBone} and its associated child bones
     * <p>
     * <b><u>NOTE:</u></b> Like all render operations, this is called exclusively in the render pipeline process.<br>
     * No modifications to the renderer can be made here, this is purely for rendering.
     */
    default void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState,
                            int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();
        RenderUtil.prepMatrixForBone(poseStack, bone);
        renderCubesOfBone(renderState, bone, poseStack, buffer, cameraState, packedLight, packedOverlay, renderColor);
        renderChildBones(renderState, bone, poseStack, buffer, cameraState, packedLight, packedOverlay, renderColor);
        poseStack.popPose();
    }

    /**
     * Renders the {@link GeoCube GeoCubes} associated with a given {@link GeoBone}
     * <p>
     * <b><u>NOTE:</u></b> Like all render operations, this is called exclusively in the render pipeline process.<br>
     * No modifications to the renderer can be made here, this is purely for rendering.
     */
    default void renderCubesOfBone(R renderState, GeoBone bone, PoseStack poseStack, VertexConsumer buffer, CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        if (bone.isHidden())
            return;

        for (GeoCube cube : bone.getCubes()) {
            poseStack.pushPose();
            renderCube(renderState, cube, poseStack, buffer, cameraState, packedLight, packedOverlay, renderColor);
            poseStack.popPose();
        }
    }

    /**
     * Render the child bones of a given {@link GeoBone}
     * <p>
     * Note that this does not render the bone itself. That should be done through {@link GeoRenderer#renderCubesOfBone} separately
     * <p>
     * <b><u>NOTE:</u></b> Like all render operations, this is called exclusively in the render pipeline process.<br>
     * No modifications to the renderer can be made here, this is purely for rendering.
     */
    default void renderChildBones(R renderState, GeoBone bone, PoseStack poseStack, VertexConsumer buffer, CameraRenderState cameraState,
                                  int packedLight, int packedColor, int renderColor) {
        if (bone.isHidingChildren())
            return;

        for (GeoBone childBone : bone.getChildBones()) {
            renderBone(renderState, poseStack, childBone, buffer, cameraState, packedLight, packedColor, renderColor);
        }
    }

    /**
     * Renders an individual {@link GeoCube}
     * <p>
     * This tends to be called recursively from something like {@link GeoRenderer#renderCubesOfBone}
     * <p>
     * <b><u>NOTE:</u></b> Like all render operations, this is called exclusively in the render pipeline process.<br>
     * No modifications to the renderer can be made here, this is purely for rendering.
     */
    default void renderCube(R renderState, GeoCube cube, PoseStack poseStack, VertexConsumer buffer, CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        RenderUtil.translateToPivotPoint(poseStack, cube);
        RenderUtil.rotateMatrixAroundCube(poseStack, cube);
        RenderUtil.translateAwayFromPivotPoint(poseStack, cube);

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = new Matrix4f(poseStack.last().pose());

        for (GeoQuad quad : cube.quads()) {
            if (quad == null)
                continue;

            Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));

            RenderUtil.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(renderState, quad, poseState, normal, buffer, packedOverlay, packedLight, renderColor);
        }
    }

    /**
     * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link VertexConsumer buffer} for rendering
     * <p>
     * <b><u>NOTE:</u></b> Like all render operations, this is called exclusively in the render pipeline process.<br>
     * No modifications to the renderer can be made here, this is purely for rendering.
     */
    default void createVerticesOfQuad(R renderState, GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
                                      int packedOverlay, int packedLight, int renderColor) {
        for (GeoVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

            buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor, vertex.texU(),
                             vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }
}
