package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.BiConsumer;

/**
 * Built-in GeoLayer for rendering a custom texture for a specific bone.
 * <p>
 * Due to the way Mojang handles {@link VertexConsumer buffers}, and for safety; this layer only supports one bone at a time.
 * Add multiple copies of this layer if your model has multiple bones you want to render with a custom texture
 */
public class CustomBoneTextureGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    protected final String boneName;
    protected final ResourceLocation texture;

    public CustomBoneTextureGeoLayer(GeoRenderer<T, O, R> renderer, String boneName, ResourceLocation texture) {
        super(renderer);

        this.boneName = boneName;
        this.texture = texture;
    }

    /**
     * Get the texture resource path for the given {@link GeoRenderState}.
     */
    @Override
    protected ResourceLocation getTextureResource(R renderState) {
        return this.texture;
    }

    /**
     * Get the render type for the render pass
     */
    protected RenderType getRenderType(R renderState, ResourceLocation texture) {
        return this.renderer.getRenderType(renderState, texture);
    }

    /**
     * Register per-bone render operations, to be rendered after the main model is done.
     * <p>
     * Even though the task is called after the main model renders, the {@link PoseStack} provided will be posed as if the bone
     * is currently rendering.
     *
     * @param consumer The registrar to accept the per-bone render tasks
     */
    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, boolean didRenderModel, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (didRenderModel)
            model.getBone(this.boneName).ifPresent(bone -> consumer.accept(bone, this::renderBone));
    }

    /**
     * Render the bone with the replacement texture
     */
    protected void renderBone(R renderState, PoseStack poseStack, GeoBone bone, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                              int packedLight, int packedOverlay, int renderColor) {
        ResourceLocation boneTexture = getTextureResource(renderState);
        ResourceLocation baseTexture = this.renderer.getTextureLocation(renderState);
        IntIntPair boneTextureSize = RenderUtil.getTextureDimensions(boneTexture);
        IntIntPair baseTextureSize = RenderUtil.getTextureDimensions(baseTexture);
        float widthRatio = baseTextureSize.firstInt() / (float)boneTextureSize.firstInt();
        float heightRatio = baseTextureSize.secondInt() / (float)boneTextureSize.secondInt();

        renderTasks.submitCustomGeometry(poseStack, getRenderType(renderState, boneTexture), (pose, buffer) -> {
            PoseStack poseStack2 = new PoseStack();

            poseStack2.last().set(pose);
            bone.setHidden(false);
            bone.setChildrenHidden(true);

            for (GeoCube cube : bone.getCubes()) {
                poseStack2.pushPose();
                renderCube(renderState, cube, poseStack2, buffer, widthRatio, heightRatio, packedLight, packedOverlay, renderColor);
                poseStack2.popPose();
            }

            bone.setHidden(false);
        });
    }

    /**
     * Renders an individual {@link GeoCube}
     * <p>
     * This tends to be called recursively from something like {@link GeoRenderer#renderCubesOfBone}
     */
    @ApiStatus.Internal
    protected void renderCube(R renderState, GeoCube cube, PoseStack poseStack, VertexConsumer buffer, float widthRatio, float heightRatio,
                              int packedLight, int packedOverlay, int renderColor) {
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
            createVerticesOfQuad(renderState, quad, poseState, normal, buffer, widthRatio, heightRatio, packedOverlay, packedLight, renderColor);
        }
    }

    /**
     * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link VertexConsumer buffer} for rendering
     */
    @ApiStatus.Internal
    protected void createVerticesOfQuad(R renderState, GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
                                        float widthRatio, float heightRatio, int packedOverlay, int packedLight, int renderColor) {
        for (GeoVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

            buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor, vertex.texU() * widthRatio, vertex.texV() * heightRatio,
                             packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }

    /**
     * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called
     * <p>
     * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones.
     * <p>
     * <b><u>NOTE:</u></b> Changing VertexConsumers or RenderTypes must not be performed here<br>
     * <b><u>NOTE:</u></b> If the passed {@link VertexConsumer buffer} is null, then the animatable was not actually rendered (invisible, etc)
     * and you may need to factor this in to your design
     */
    @ApiStatus.Internal
    @Override
    public void preRender(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
        if (didRenderModel) {
            bakedModel.getBone(this.boneName).ifPresent(bone -> {
                bone.setHidden(true);
                bone.setChildrenHidden(false);
            });
        }
    }
}
