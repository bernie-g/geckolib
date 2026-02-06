package software.bernie.geckolib.renderer.layer.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.cache.model.GeoQuad;
import software.bernie.geckolib.cache.model.GeoVertex;
import software.bernie.geckolib.cache.model.cuboid.CuboidGeoBone;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.BiConsumer;

/// Built-in GeoLayer for rendering a custom texture for a specific bone.
///
/// Due to the way Mojang handles [buffers][VertexConsumer], and for safety; this layer only supports one bone at a time.
/// Add multiple copies of this layer if your model has multiple bones you want to render with a custom texture
///
/// @param <T> Animatable class type. Inherited from the renderer this layer is attached to
/// @param <O> Associated object class type, or [Void] if none. Inherited from the renderer this layer is attached to
/// @param <R> RenderState class type. Inherited from the renderer this layer is attached to
public class CustomBoneTextureGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    protected final String boneName;
    protected final Identifier texture;

    public CustomBoneTextureGeoLayer(GeoRenderer<T, O, R> renderer, String boneName, Identifier texture) {
        super(renderer);

        this.boneName = boneName;
        this.texture = texture;
    }

    /// Get the texture resource path for the given [GeoRenderState].
    @Override
    protected Identifier getTextureResource(R renderState) {
        return this.texture;
    }

    /// Get the render type for the render pass
    protected @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return this.renderer.getRenderType(renderState, texture);
    }

    /// This method is called by the [GeoRenderer] before rendering, immediately after [GeoRenderer#preRenderPass] has been called
    ///
    /// This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones.
    ///
    /// **<u>NOTE:</u>** Changing VertexConsumers or RenderTypes must not be performed here
    @ApiStatus.Internal
    @Override
    public void preRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (renderPassInfo.willRender()) {
            renderPassInfo.addBoneUpdater((renderPassInfo1, snapshots) -> snapshots.get(CustomBoneTextureGeoLayer.this.boneName)
                    .ifPresent(bone -> {
                        bone.skipRender(true);
                        bone.skipChildrenRender(false);
                    }));
        }
    }

    /// Register per-bone render operations, to be rendered after the main model is done.
    ///
    /// Even though the task is called after the main model renders, the [PoseStack] provided will be posed as if the bone
    /// is currently rendering.
    ///
    /// @param renderPassInfo The collated render-related data for this render pass
    /// @param consumer The registrar to accept the per-bone render tasks
    @Override
    public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (renderPassInfo.willRender()) {
            renderPassInfo.model().getBone(this.boneName).filter(CuboidGeoBone.class::isInstance)
                    .ifPresentOrElse(bone -> consumer.accept(bone, this::renderBone),
                                     () -> GeckoLibConstants.LOGGER.error("Unable to find bone for CustomBoneTextureGeoLayer: {}, skipping", this.boneName));
        }
    }

    /// Render the bone with the replacement texture
    protected void renderBone(RenderPassInfo<R> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks) {
        R renderState = renderPassInfo.renderState();
        Identifier boneTexture = getTextureResource(renderState);
        Identifier baseTexture = this.renderer.getTextureLocation(renderState);
        IntIntPair boneTextureSize = RenderUtil.getTextureDimensions(boneTexture);
        IntIntPair baseTextureSize = RenderUtil.getTextureDimensions(baseTexture);
        float widthRatio = baseTextureSize.firstInt() / (float)boneTextureSize.firstInt();
        float heightRatio = baseTextureSize.secondInt() / (float)boneTextureSize.secondInt();
        int packedLight = renderPassInfo.packedLight();
        int packedOverlay = renderPassInfo.packedOverlay();
        int renderColor = renderPassInfo.renderColor();
        RenderType renderType = getRenderType(renderState, boneTexture);

        if (renderType != null) {
            renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, buffer) -> {
                PoseStack poseStack = new PoseStack();

                poseStack.last().set(pose);
                poseStack.pushPose();
                RenderUtil.prepMatrixForBone(poseStack, bone);
                bone.updateBonePositionListeners(poseStack, renderPassInfo);

                for (GeoCube cube : ((CuboidGeoBone)bone).cubes) {
                    renderCube(cube, poseStack, buffer, packedLight, packedOverlay, renderColor, widthRatio, heightRatio);
                }

                poseStack.popPose();
            });
        }
    }

    /// Submit a cube's modified quads to the vertex consumer
    /// @see GeoCube#render(PoseStack, VertexConsumer, int, int, int)
    @ApiStatus.Internal
    public void renderCube(GeoCube cube, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor, float widthRatio, float heightRatio) {
        cube.translateToPivotPoint(poseStack);
        cube.rotate(poseStack);
        cube.translateAwayFromPivotPoint(poseStack);

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = new Matrix4f(poseStack.last().pose());

        for (GeoQuad quad : cube.quads()) {
            if (quad == null)
                continue;

            Vector3f normal = normalisedPoseState.transform(quad.normalVec());

            RenderUtil.fixInvertedFlatCube(cube, normal);
            renderQuad(quad, poseState, normal, vertexConsumer, packedLight, packedOverlay, renderColor, widthRatio, heightRatio);
        }
    }

    /// Submit a modified texture quad to the vertex consumer
    /// @see GeoQuad#render(Matrix4f, Vector3f, VertexConsumer, int, int, int)
    @ApiStatus.Internal
    protected void renderQuad(GeoQuad quad, Matrix4f pose, Vector3f normal, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int renderColor, float widthRatio, float heightRatio) {
        for (GeoVertex vertex : quad.vertices()) {
            Vector4f vector4f = pose.transform(new Vector4f(vertex.posX(), vertex.posY(), vertex.posZ(), 1));

            vertexConsumer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), renderColor, vertex.texU() * widthRatio,
                                     vertex.texV() * heightRatio, packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }
}
