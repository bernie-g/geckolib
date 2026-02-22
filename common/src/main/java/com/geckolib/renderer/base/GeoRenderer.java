package com.geckolib.renderer.base;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.GeoItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.WalkAnimationState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.loading.math.MathParser;
import com.geckolib.loading.math.value.Variable;
import com.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.ToDoubleFunction;

/// Base interface for all GeckoLib renderers.
///
/// @param <T> The type of animatable this renderer is for
/// @param <O> The associated object this renderer takes when extracting data, or void if not applicable
/// @param <R> The type of render state this renderer uses
/// @see GeoRendererInternals
public non-sealed interface GeoRenderer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRendererInternals<T, O, R> {
	/// Gets a tint-applying color to render the given animatable with
	///
	/// Returns opaque white by default
	@Override
	default int getRenderColor(T animatable, @Nullable O relatedObject, float partialTick) {
		return 0xFFFFFFFF;
	}

	/// Gets a packed overlay coordinate pair for rendering
	///
	/// Mostly just used for the red tint when an entity is hurt,
	/// but can be used for other things like the [net.minecraft.world.entity.monster.Creeper]
	/// white tint when exploding.
	@Override
	default int getPackedOverlay(T animatable, @Nullable O relatedObject, float u, float partialTick) {
		return OverlayTexture.NO_OVERLAY;
	}

	/// Determines the threshold value before the animatable should be considered moving for animation purposes
	///
	/// The default value and usage for this varies depending on the renderer
	///
	///   - For entities, it represents the lateral velocity of the object or the current speed of the [WalkAnimationState]
	///   - For [Tile Entities][GeoBlockEntity] and [Items][GeoItem], it's currently unused
	///
	/// The lower the value, the more sensitive the [AnimationTest#isMoving()] check will be.
	///
	/// Particularly low values may have adverse effects
	default float getMotionAnimThreshold(T animatable) {
		return 0.015f;
	}

	/// Gets the [RenderType] to render the current render pass with
	///
	/// Uses the [RenderTypes#entityCutout] `RenderType` by default
	///
	/// Override this to change the way a model will render (such as translucent models, etc.)
	///
	/// @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions from taking place
	default @Nullable RenderType getRenderType(R renderState, Identifier texture) {
		return RenderTypes.entityCutout(texture);
	}

	/// Override to add any custom [DataTicket]s you need to capture for rendering.
	///
	/// The animatable is discarded from the rendering context after this, so any data needed
	/// for rendering should be captured in the renderState provided
	///
	/// @param animatable The animatable instance being rendered
	/// @param relatedObject An object related to the render pass or null if not applicable.
	///                         (E.G., ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	/// @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
	/// @param partialTick The fraction of a tick that has elapsed as of the current render pass
	@ApiStatus.OverrideOnly
    default void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {}

    /// This method is called once per render-frame for each [GeoAnimatable] being rendered
    ///
    /// Use this method to set custom [Variable][Variable] values via
    /// [MathParser.setVariable][MathParser#setVariable(String, ToDoubleFunction)]
    @Override
    default void setMolangQueryValues(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {}

    /// Initial access point for performing a single render pass; it all begins here.
    /// The [GeoRenderState] should have already been filled by this stage.
    ///
    /// All GeckoLib renderers should immediately defer their respective default `submit` calls to this, for consistent handling
    /// @see #performRenderPass(GeoRenderState, PoseStack, SubmitNodeCollector, CameraRenderState, RenderPassInfo.BoneUpdater)
    default void performRenderPass(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        performRenderPass(renderState, poseStack, renderTasks, cameraState, null);
    }

	/// Initial access point for performing a single render pass, with an optional pre-defined [RenderPassInfo.BoneUpdater] to allow for pre-positioning models from outside the renderer
	default void performRenderPass(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState, RenderPassInfo.@Nullable BoneUpdater<R> boneUpdater) {
		poseStack.pushPose();

        final RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));
        final RenderPassInfo<R> renderPassInfo = RenderPassInfo.create(this, renderState, poseStack, cameraState, renderType != null);

        if (boneUpdater != null)
            renderPassInfo.addBoneUpdater(boneUpdater);

		if (firePreRenderEvent(renderPassInfo, renderTasks)) {
            preRenderPass(renderPassInfo, renderTasks);
            scaleModelForRender(renderPassInfo, 1, 1);
            adjustRenderPose(renderPassInfo);
			preApplyRenderLayers(renderPassInfo, renderTasks);
            renderPassInfo.captureModelRenderPose();
			submitRenderTasks(renderPassInfo, renderTasks, renderType);
            submitPerBoneRenderTasks(renderPassInfo, renderTasks);
			applyRenderLayers(renderPassInfo, renderTasks);
		}

		poseStack.popPose();

		postRenderPass(renderPassInfo, renderTasks);
	}

	/// Called at the start of the render compilation pass. PoseState manipulations have not yet taken place and typically should not be made here.
	///
	/// Manipulation of the model's bones is not permitted here
	///
	/// Use this method to handle any preparation or pre-work required for the render submission.
	///
	/// @see #scaleModelForRender
	/// @see #adjustRenderPose
	/// @see #adjustModelBonesForRender
	default void preRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {}

    /// Scales the [PoseStack] in preparation for rendering the model, excluding when re-rendering the model as part of a [GeoRenderLayer] or external render call
    ///
    /// Override and call `super` with modified scale values as needed to further modify the scale of the model
    default void scaleModelForRender(RenderPassInfo<R> renderPassInfo, float widthScale, float heightScale) {
        if (widthScale != 1 || heightScale != 1)
            renderPassInfo.poseStack().scale(widthScale, heightScale, widthScale);
    }

    /// Transform the [PoseStack] in preparation for rendering the model.
    ///
    /// This is called after [#scaleModelForRender], and so any transformations here will be scaled appropriately.
    /// If you need to do pre-scale translations, use [#preRenderPass]
    ///
    /// PoseStack translations made here are kept until the end of the render process
    default void adjustRenderPose(RenderPassInfo<R> renderPassInfo) {}

    /// Perform any necessary adjustments of the model here, such as positioning/scaling/rotating or hiding bones.
    ///
    /// No manipulation of the RenderState is permitted here
    default void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {}

    /// Build and submit the actual render task to the [OrderedSubmitNodeCollector] here.
    ///
    /// Once the render task has been submitted here, no further manipulations of the render pass should be made.
    ///
    /// If the provided [RenderType] is null, no submission will be made
    default void submitRenderTasks(RenderPassInfo<R> renderPassInfo, OrderedSubmitNodeCollector renderTasks, @Nullable RenderType renderType) {
        if (renderType == null)
            return;

        final int packedLight = renderPassInfo.packedLight();
        final int packedOverlay = renderPassInfo.packedOverlay();
        final int renderColor = renderPassInfo.renderColor();
		final BakedGeoModel model = renderPassInfo.model();

		if (model.isMissingno()) {
			submitMissingModelRender(renderPassInfo, renderTasks);

			return;
		}

        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> {
            final PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().set(pose);
            renderPassInfo.renderPosed(() -> renderPassInfo.model().render(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor));
            poseStack.popPose();
        });
    }

	/// Called after the rest of the render pass has completed, including discarding the PoseStack's pose.
	///
	/// The actual rendering of the object has not yet taken place, as that is done in a deferred [submission][#performRenderPass]
	default void postRenderPass(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {}
}
