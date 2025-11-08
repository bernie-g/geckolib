package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.WalkAnimationState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.internal.PerBoneRenderTasks;
import software.bernie.geckolib.renderer.internal.RenderModelPositioner;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;
import java.util.Map;

/**
 * Base interface for all GeckoLib renderers.<br>
 *
 * @param <T> The type of animatable this renderer is for
 * @param <O> The associated object this renderer takes when extracting data, or void if not applicable
 * @param <R> The type of render state this renderer uses
 */
public interface GeoRenderer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoModelRenderer<R> {
	/**
	 * Gets the model instance for this renderer
	 */
	GeoModel<T> getGeoModel();

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	default List<GeoRenderLayer<T, O, R>> getRenderLayers() {
		return List.of();
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * <p>
	 * You generally shouldn't need to override this
	 *
	 * @param animatable The Animatable instance being renderer
	 * @param relatedObject An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 */
	@ApiStatus.Internal
	default long getInstanceId(T animatable, O relatedObject) {
		return animatable.hashCode();
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns white by default
	 */
	default int getRenderColor(T animatable, O relatedObject, float partialTick) {
		return 0xFFFFFFFF;
	}

	/**
	 * Gets a packed overlay coordinate pair for rendering
	 * <p>
	 * Mostly just used for the red tint when an entity is hurt,
	 * but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper}
	 * white tint when exploding.
	 */
	default int getPackedOverlay(T animatable, O relatedObject, float u, float partialTick) {
		return OverlayTexture.NO_OVERLAY;
	}

	/**
	 * Determines the threshold value before the animatable should be considered moving for animation purposes
	 * <p>
	 * The default value and usage for this varies depending on the renderer
	 * <ul>
	 *     <li>For entities, it represents the lateral velocity of the object or the current speed of the {@link WalkAnimationState}</li>
	 *     <li>For {@link software.bernie.geckolib.animatable.GeoBlockEntity Tile Entities} and {@link software.bernie.geckolib.animatable.GeoItem Items}, it's currently unused</li>
	 *</ul>
	 *
	 *
	 * The lower the value, the more sensitive the {@link AnimationTest#isMoving()} check will be.
	 * <p>
	 * Particularly low values may have adverse effects
	 */
	default float getMotionAnimThreshold(T animatable) {
		return 0.015f;
	}

	/**
	 * Gets the texture resource location to render for the given animatable
	 */
	default ResourceLocation getTextureLocation(R renderState) {
		return getGeoModel().getTextureResource(renderState);
	}

	/**
	 * Gets the {@link RenderType} to render the current render pass with
	 * <p>
	 * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default
	 * <p>
	 * Override this to change the way a model will render (such as translucent models, etc)
	 *
	 * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions from taking place
	 */
	@Nullable
	default RenderType getRenderType(R renderState, ResourceLocation texture) {
		return getGeoModel().getRenderType(renderState, texture);
	}

	/**
	 * Override to add any custom {@link DataTicket}s you need to capture for rendering.
	 * <p>
	 * The animatable is discarded from the rendering context after this, so any data needed
	 * for rendering should be captured in the renderState provided
	 *
	 * @param animatable The animatable instance being rendered
	 * @param relatedObject An object related to the render pass or null if not applicable.
	 *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
	 * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
     * @param partialTick The fraction of a tick that has elapsed as of the current render pass
	 */
	@ApiStatus.OverrideOnly
	default void addRenderData(T animatable, O relatedObject, R renderState, float partialTick) {}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	default R captureDefaultRenderState(T animatable, O relatedObject, R renderState, float partialTick) {
		long instanceId = getInstanceId(animatable, relatedObject);

		renderState.addGeckolibData(DataTickets.TICK, animatable.getTick(relatedObject));
		renderState.addGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, instanceId);
		renderState.addGeckolibData(DataTickets.ANIMATABLE_MANAGER, animatable.getAnimatableInstanceCache().getManagerForId(instanceId));
		renderState.addGeckolibData(DataTickets.PARTIAL_TICK, partialTick);
		renderState.addGeckolibData(DataTickets.RENDER_COLOR, getRenderColor(animatable, relatedObject, partialTick));
		renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, getPackedOverlay(animatable, relatedObject, 0, partialTick));
		renderState.addGeckolibData(DataTickets.IS_MOVING, false);
		renderState.addGeckolibData(DataTickets.BONE_RESET_TIME, animatable.getBoneResetTime());
		renderState.addGeckolibData(DataTickets.ANIMATABLE_CLASS, animatable.getClass());
		renderState.addGeckolibData(DataTickets.PER_BONE_TASKS, PerBoneRenderTasks.create());

		return renderState;
	}

    /**
     * Called to create the {@link GeoRenderState} for this render pass
     */
    R createRenderState(T animatable, O relatedObject);

    /**
     * Construct the {@link AnimationState} for the given render pass, ready to pass onto the {@link GeoModel} for handling
     */
    default AnimationState<T> createAnimationState(R renderState) {
        return new AnimationState<>(renderState);
    }

	/**
	 * Create the {@link GeoRenderState} for the upcoming render pass
	 * <p>
	 * This is an internal method only; you should <b><u>NOT</u></b> be overriding this<br>
	 * Override {@link #addRenderData(GeoAnimatable, Object, GeoRenderState, float)} instead
	 */
	@ApiStatus.Internal
	default R fillRenderState(T animatable, O relatedObject, R renderState, float partialTick) {
		captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
		addRenderData(animatable, relatedObject, renderState, partialTick);

		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			renderLayer.addRenderData(animatable, relatedObject, renderState, partialTick);
		}

		fireCompileRenderStateEvent(animatable, relatedObject, renderState, partialTick);
		getGeoModel().prepareForRenderPass(animatable, renderState, partialTick);

		return renderState;
	}

    /**
     * Initial access point for performing a single render pass; it all begins here.<br>
     * The {@link GeoRenderState} should have already been filled by this stage.
     * <p>
     * All GeckoLib renderers should immediately defer their respective default {@code submit} calls to this, for consistent handling
     * @see #performRenderPass(R, PoseStack, SubmitNodeCollector, CameraRenderState, RenderModelPositioner)
     */
    default void performRenderPass(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
        performRenderPass(renderState, poseStack, renderTasks, cameraState, null);
    }

	/**
	 * Initial access point for performing a single render pass; it all begins here.<br>
	 * The {@link GeoRenderState} should have already been filled by this stage.
	 * <p>
	 * All GeckoLib renderers should immediately defer their respective default {@code submit} calls to this, for consistent handling
	 */
	default void performRenderPass(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                   @Nullable RenderModelPositioner<R> modelPositioner) {
		poseStack.pushPose();

		final GeoModel<T> geoModel = getGeoModel();
		final BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(renderState));
        final int packedLight = renderState.getPackedLight();
        final int packedOverlay = renderState.getGeckolibData(DataTickets.PACKED_OVERLAY);
		final int renderColor = renderState.getGeckolibData(DataTickets.RENDER_COLOR);
        final RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));

        renderState.addGeckolibData(DataTickets.OBJECT_RENDER_POSE, new Matrix4f(poseStack.last().pose()));

		if (firePreRenderEvent(renderState, poseStack, model, renderTasks, cameraState)) {
            preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
            scaleModelForRender(renderState, 1, 1, poseStack, model, cameraState);
            adjustRenderPose(renderState, poseStack, model, cameraState);
			preApplyRenderLayers(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, renderType != null);
            renderState.addGeckolibData(DataTickets.MODEL_RENDER_POSE, new Matrix4f(poseStack.last().pose()));
			submitRenderTasks(renderState, poseStack, model, renderTasks, cameraState, geoModel, renderType, modelPositioner, packedLight, packedOverlay, renderColor);
			applyRenderLayers(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, renderType != null);
		}

		poseStack.popPose();

		renderFinal(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Called at the start of the render compilation pass. PoseState manipulations have not yet taken place and typically should not be made here.
	 * <p>
	 * Use this method to handle any preparation or pre-work required for the render submission.
     * <p>
     * Manipulation of the model's bones is not permitted here
	 */
	default void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
						   int packedLight, int packedOverlay, int renderColor) {}

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * Override and call <code>super</code> with modified scale values as needed to further modify the scale of the model
     */
    default void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
        if (widthScale != 1 || heightScale != 1)
            poseStack.scale(widthScale, heightScale, widthScale);
    }

    /**
     * Transform the {@link PoseStack} in preparation for rendering the model.
     * <p>
     * This is called after {@link #scaleModelForRender}, and so any transformations here will be scaled appropriately.
     * If you need to do pre-scale translations, use {@link #preRender}
     * <p>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    default void adjustRenderPose(R renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {}

    /**
     * Perform any necessary adjustments of the model here, such as positioning/scaling/rotating bones.
     * <p>
     * No manipulation of the RenderState is permitted here
     */
    default void positionModelForRender(R renderState, BakedGeoModel model, CameraRenderState cameraState) {}

    /**
     * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#preRender pre-render} actions.
     */
    default void preApplyRenderLayers(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                      int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
        final PerBoneRenderTasks.ForRenderer<R> perBoneTasks = getPerBoneTasks(renderState);

        for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
            renderLayer.preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, didRenderModel);
            renderLayer.addPerBoneRender(renderState, model, didRenderModel, perBoneTasks::addTask);
        }
    }

    /**
     * Build and submit the actual render task to the {@link OrderedSubmitNodeCollector} here.
     * <p>
     * Once the render task has been submitted here, no further manipulations of the render pass should be made.
     * <p>
     * If the provided {@link RenderType} is null, no submission will be made
     */
    default void submitRenderTasks(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, OrderedSubmitNodeCollector renderTasks,
                                   CameraRenderState cameraState, GeoModel<T> model, @Nullable RenderType renderType,
                                   @Nullable RenderModelPositioner<R> modelPositioner, int packedLight, int packedOverlay, int renderColor) {
        if (renderType == null)
            return;

        RenderModelPositioner<R> callback = RenderModelPositioner.add(modelPositioner, (renderState2, model2) -> {
            positionModelForRender(renderState2, model2, cameraState);
            model.handleAnimations(createAnimationState(renderState2));
        });

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            final PoseStack poseStack2 = new PoseStack();

            poseStack2.last().set(pose);
            callback.run(renderState, bakedModel);

            for (GeoBone bone : bakedModel.topLevelBones()) {
                renderBone(renderState, poseStack2, bone, vertexConsumer, cameraState, packedLight, packedOverlay, renderColor);
            }
        });
    }

    /**
     * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
     */
    default void applyRenderLayers(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                   int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
        final PerBoneRenderTasks.ForRenderer<R> perBoneTasks = getPerBoneTasks(renderState);

        if (!perBoneTasks.isEmpty())
            submitPerBoneRenderTasks(renderState, poseStack, perBoneTasks, renderTasks, cameraState, packedLight, packedOverlay, renderColor);

        for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
            renderLayer.submitRenderTask(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, didRenderModel);
        }
    }

	/**
	 * Called after all other render pass work has taken place, including reverting the {@link PoseStack}'s state
     * <p>
     * The actual rendering of the object has not yet taken place, as that is done in a deferred {@link #performRenderPass submission}
	 */
	default void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
							 int packedLight, int packedOverlay, int renderColor) {}

    /**
     * Submit the registered {@link PerBoneRender} tasks that have been submitted for this render pass
     */
    default void submitPerBoneRenderTasks(R renderState, PoseStack poseStack, PerBoneRenderTasks.ForRenderer<R> perBoneTasks,
                                          SubmitNodeCollector renderTasks, CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        final Matrix4f pose = renderState.getGeckolibData(DataTickets.MODEL_RENDER_POSE);

        poseStack.pushPose();
        poseStack.last().pose().set(pose);

        for (Map.Entry<GeoBone, List<PerBoneRender<R>>> boneTasks : perBoneTasks) {
            poseStack.pushPose();
            boneTasks.getKey().transformToBone(poseStack);

            for (PerBoneRender<R> renderOp : boneTasks.getValue()) {
                poseStack.pushPose();
                renderOp.submitRenderTask(renderState, poseStack, boneTasks.getKey(), renderTasks, cameraState, packedLight, packedOverlay, renderColor);
                poseStack.popPose();
            }

            poseStack.popPose();
        }

        poseStack.popPose();
    }

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	void fireCompileRenderLayersEvent();

	/**
	 * Create and fire the relevant {@code CompileRenderState} event hook for this renderer
	 */
	void fireCompileRenderStateEvent(T animatable, O relatedObject, R renderState, float partialTick);

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

	/**
	 * Internal helper method to help with type-resolution of the {@link DataTickets#PER_BONE_TASKS} DataTicket
	 */
    default PerBoneRenderTasks.ForRenderer<R> getPerBoneTasks(R renderState) {
		return PerBoneRenderTasks.get(renderState, this);
	}
}
