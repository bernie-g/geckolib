package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/// Split-off interface containing only the internals of [GeoRenderer]
/// so that the intended public-facing API is much more visible.
///
/// Methods in this interface may still be used or extended unless otherwise noted.
/// This simply allows for hiding less-relevant API content from the typical user
public sealed interface GeoRendererInternals<T extends GeoAnimatable, O, R extends GeoRenderState> permits GeoRenderer {
    /// Gets the model instance for this renderer
    GeoModel<T> getGeoModel();

    /// Returns the list of registered [GeoRenderLayers][GeoRenderLayer] for this renderer
    default List<GeoRenderLayer<T, O, R>> getRenderLayers() {
        return List.of();
    }

    /// Gets a tint-applying color to render the given animatable with
    ///
    /// Returns opaque white by default
    int getRenderColor(T animatable, @Nullable O relatedObject, float partialTick);

    /// Gets a packed overlay coordinate pair for rendering
    ///
    /// Mostly just used for the red tint when an entity is hurt,
    /// but can be used for other things like the [net.minecraft.world.entity.monster.Creeper]
    /// white tint when exploding.
    int getPackedOverlay(T animatable, @Nullable O relatedObject, float u, float partialTick);

    /// Gets the id that represents the current animatable's instance for animation purposes.
    ///
    /// @param animatable The Animatable instance being renderer
    /// @param relatedObject An object related to the render pass or null if not applicable.
    ///                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
    @ApiStatus.OverrideOnly
    default long getInstanceId(T animatable, @Nullable O relatedObject) {
        return animatable.hashCode();
    }

    /// Gets the texture resource location to render for the given animatable
    default Identifier getTextureLocation(R renderState) {
        return getGeoModel().getTextureResource(renderState);
    }

    /// Internal method for capturing the common RenderState data for all animatable objects
    @ApiStatus.Internal
    default void captureDefaultRenderState(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        long instanceId = getInstanceId(animatable, relatedObject);

        renderState.addGeckolibData(DataTickets.TICK, ClientUtil.getCurrentTick());
        renderState.addGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, instanceId);
        renderState.addGeckolibData(DataTickets.ANIMATABLE_MANAGER, animatable.getAnimatableInstanceCache().getManagerForId(instanceId));
        renderState.addGeckolibData(DataTickets.PARTIAL_TICK, partialTick);
        renderState.addGeckolibData(DataTickets.RENDER_COLOR, getRenderColor(animatable, relatedObject, partialTick));
        renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, getPackedOverlay(animatable, relatedObject, 0, partialTick));
        renderState.addGeckolibData(DataTickets.IS_MOVING, false);
        renderState.addGeckolibData(DataTickets.ANIMATABLE_CLASS, animatable.getClass());
    }

    /// Called to create the [GeoRenderState] for this render pass
    R createRenderState(T animatable, @Nullable O relatedObject);

    /// Override to add any custom [DataTicket]s you need to capture for rendering.
    ///
    /// The animatable is discarded from the rendering context after this, so any data needed
    /// for rendering should be captured in the renderState provided
    ///
    /// @param animatable The animatable instance being rendered
    /// @param relatedObject An object related to the render pass or null if not applicable.
    ///                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
    /// @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
    /// @param partialTick The fraction of a tick that has elapsed as of the current render pass
    @ApiStatus.OverrideOnly
    void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick);

    /// This method is called once per render-frame for each [GeoAnimatable] being rendered
    ///
    /// Use this method to set custom [Variable][Variable] values via
    /// [MathParser.setVariable][MathParser#setVariable(String, ToDoubleFunction)]
    void setMolangQueryValues(T animatable, @Nullable O relatedObject, R renderState, float partialTick);

    /// Create the [GeoRenderState] for the upcoming render pass
    ///
    /// This is an internal method only; you should **<u>NOT</u>** be overriding this
    /// Override [#addRenderData(GeoAnimatable, Object, GeoRenderState, float)] instead
    @ApiStatus.NonExtendable
    default R fillRenderState(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
        addRenderData(animatable, relatedObject, renderState, partialTick);
        getGeoModel().addAdditionalStateData(animatable, relatedObject, renderState);

        for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
            renderLayer.addRenderData(animatable, relatedObject, renderState, partialTick);
        }

        fireCompileRenderStateEvent(animatable, relatedObject, renderState, partialTick);
        setMolangQueryValues(animatable, relatedObject, renderState, partialTick);
        AnimationProcessor.extractControllerStates(animatable, renderState, getGeoModel());

        return renderState;
    }

    /// Calls back to the various [RenderLayers][GeoRenderLayer] that have been registered to this renderer for their [pre-render][GeoRenderLayer#preRender] actions.
    default void preApplyRenderLayers(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
            renderLayer.preRender(renderPassInfo, renderTasks);
            renderLayer.addPerBoneRender(renderPassInfo, renderPassInfo::addPerBoneRender);
        }
    }

    /// Render the various [RenderLayers][GeoRenderLayer] that have been registered to this renderer
    default void applyRenderLayers(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
            renderLayer.submitRenderTask(renderPassInfo, renderTasks);
        }
    }

    /// Apply the animation modifications from the relevant [AnimationController]s for this render pass
    default void applyAnimationControllers(RenderPassInfo<R> renderPassInfo, BoneSnapshots boneSnapshots) {
        ControllerState[] controllerStates = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ANIMATION_CONTROLLER_STATES, new ControllerState[0]);

        for (int i = 0; i < controllerStates.length; i++) {
            AnimationProcessor.createBoneSnapshots(controllerStates[i], boneSnapshots);
        }
    }

    /// Submit the registered [PerBoneRender] tasks that have been submitted for this render pass
    default void submitPerBoneRenderTasks(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        final Map<GeoBone, List<PerBoneRender<R>>> perBoneRenders = renderPassInfo.getBoneRenderTasks();

        if (perBoneRenders.isEmpty())
            return;

        renderPassInfo.renderPosed(() -> {
            final Matrix4f pose = renderPassInfo.getModelRenderMatrixState();
            final PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().pose().set(pose);

            for (Map.Entry<GeoBone, List<PerBoneRender<R>>> boneTasks : perBoneRenders.entrySet()) {
                final GeoBone bone = boneTasks.getKey();

                poseStack.pushPose();
                RenderUtil.transformToBone(poseStack, bone);

                for (PerBoneRender<R> renderOp : boneTasks.getValue()) {
                    poseStack.pushPose();
                    renderOp.submitRenderTask(renderPassInfo, bone, renderTasks);
                    poseStack.popPose();
                }

                poseStack.popPose();
            }

            poseStack.popPose();
        });
    }

    /// Render the 'missing model' cube
    ///
    /// Should be called when a valid cannot be found for a GeckoLib render pass, to represent a missing model
    default void submitMissingModelRender(RenderPassInfo<R> renderPassInfo, OrderedSubmitNodeCollector renderTasks) {
        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), RenderTypes.entityCutout(MissingTextureAtlasSprite.getLocation()), (pose, vertexConsumer) -> {
            final PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().set(pose);
            renderPassInfo.renderPosed(() -> renderPassInfo.model().render(renderPassInfo, vertexConsumer, renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), renderPassInfo.renderColor()));
            poseStack.popPose();
        });
    }

    /// Create and fire the relevant `CompileLayers` event hook for this renderer
    void fireCompileRenderLayersEvent();

    /// Create and fire the relevant `CompileRenderState` event hook for this renderer
    void fireCompileRenderStateEvent(T animatable, @Nullable O relatedObject, R renderState, float partialTick);

    /// Create and fire the relevant `Pre-Render` event hook for this renderer
    ///
    /// @return Whether the renderer should proceed based on the cancellation state of the event
    boolean firePreRenderEvent(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks);
}
