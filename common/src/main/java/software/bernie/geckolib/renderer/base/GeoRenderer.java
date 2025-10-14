package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.WalkAnimationState;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base interface for all GeckoLib renderers.<br>
 *
 * @param <T> The type of animatable this renderer is for
 * @param <O> The associated object this renderer takes when extracting data, or void if not applicable
 */
public interface GeoRenderer<T extends GeoAnimatable, O, R extends GeoRenderState> {
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
		renderState.addGeckolibData(DataTickets.PER_BONE_TASKS, new Reference2ObjectOpenHashMap<>(0));

		return renderState;
	}

    /**
     * Called to create the {@link GeoRenderState} for this render pass
     */
    R createRenderState(T animatable, O relatedObject);

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
	 * Initial access point for submitting render tasks; it all begins here.<br>
	 * The {@link GeoRenderState} should have already been filled by this stage.
	 * <p>
	 * All GeckoLib renderers should immediately defer their respective default {@code submit} calls to this, for consistent handling
	 */
	default void submitRenderTasks(R renderState, PoseStack poseStack, SubmitNodeCollector renderTasks, CameraRenderState cameraState) {
		poseStack.pushPose();

		final GeoModel<T> geoModel = getGeoModel();
		final BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(renderState));
        final int packedLight = renderState.getPackedLight();
        final int packedOverlay = renderState.getGeckolibData(DataTickets.PACKED_OVERLAY);
		final int renderColor = renderState.getGeckolibData(DataTickets.RENDER_COLOR);
        final RenderType renderType = getRenderType(renderState, getTextureLocation(renderState));

		if (firePreRenderEvent(renderState, poseStack, model, renderTasks, cameraState)) {
            preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
            scaleModelForRender(renderState, 1, 1, poseStack, model, cameraState);
            adjustRenderPose(renderState, poseStack, model, cameraState);
            geoModel.handleAnimations(createAnimationState(renderState));
			preApplyRenderLayers(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, renderType != null);
			buildRenderTask(renderState, poseStack, model, renderTasks, cameraState, renderType, packedLight, packedOverlay, renderColor);
			applyRenderLayers(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, renderType != null);
			postRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
			firePostRenderEvent(renderState, poseStack, model, renderTasks, cameraState);
		}

		poseStack.popPose();

		renderFinal(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	default void buildRenderTask(R renderState, PoseStack poseStack, BakedGeoModel model, OrderedSubmitNodeCollector renderTasks, CameraRenderState cameraState, @Nullable RenderType renderType,
                                 int packedLight, int packedOverlay, int renderColor) {
        if (renderType == null)
            return;

        renderTasks.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) -> {
            final PoseStack poseStack2 = new PoseStack();
            final boolean skipBoneTasks = getPerBoneTasks(renderState).isEmpty();

            poseStack2.last().set(pose);

            for (GeoBone bone : model.topLevelBones()) {
                renderBone(renderState, poseStack2, bone, vertexConsumer, cameraState, skipBoneTasks, packedLight, packedOverlay, renderColor);
            }
        });
	}

	/**
	 * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#preRender pre-render} actions.
	 */
	default void preApplyRenderLayers(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
									  int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
		Reference2ObjectMap<GeoBone, Pair<MutableObject<PoseStack.Pose>, PerBoneRender<R>>> perBoneTasks = getPerBoneTasks(renderState);

		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			renderLayer.preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, didRenderModel);
			renderLayer.addPerBoneRender(renderState, model, didRenderModel, (boneName, renderOp) -> perBoneTasks.put(boneName, Pair.of(new MutableObject<>(), renderOp)));
		}
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	default void applyRenderLayers(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
								   int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
		for (Reference2ObjectMap.Entry<GeoBone, Pair<MutableObject<PoseStack.Pose>, PerBoneRender<R>>> perBoneTask : getPerBoneTasks(renderState).reference2ObjectEntrySet()) {
			perBoneTask.getValue().right().runTask(renderState, poseStack, perBoneTask.getKey(), perBoneTask.getValue().left().getValue(), renderTasks, cameraState, packedLight, packedOverlay, renderColor);
		}

		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			renderLayer.submitRenderTask(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor, didRenderModel);
		}
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	default void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
						   int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here
	 * <p>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	default void postRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
							int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * Called after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
	 */
	default void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
							 int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
     * <p>
     * <b><u>NOTE:</u></b> Like all render operations, this is called exclusively in the render pipeline process.<br>
     * No modifications to the renderer can be made here, this is purely for rendering.
	 */
	default void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState, boolean skipBoneTasks,
                            int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		RenderUtil.prepMatrixForBone(poseStack, bone);

		if (!skipBoneTasks) {
			Pair<MutableObject<PoseStack.Pose>, PerBoneRender<R>> boneRenderTask = getPerBoneTasks(renderState).get(bone);

			if (boneRenderTask != null)
				boneRenderTask.left().setValue(poseStack.last().copy());
		}

		renderCubesOfBone(renderState, bone, poseStack, buffer, cameraState, packedLight, packedOverlay, renderColor);
		renderChildBones(renderState, bone, poseStack, buffer, cameraState, skipBoneTasks, packedLight, packedOverlay, renderColor);
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
	default void renderChildBones(R renderState, GeoBone bone, PoseStack poseStack, VertexConsumer buffer, CameraRenderState cameraState, boolean skipBoneTasks,
                                  int packedLight, int packedColor, int renderColor) {
		if (bone.isHidingChildren())
			return;

		for (GeoBone childBone : bone.getChildBones()) {
			renderBone(renderState, poseStack, childBone, buffer, cameraState, skipBoneTasks, packedLight, packedColor, renderColor);
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
	 * Transform the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
     * <p>
     * This is called after {@link #scaleModelForRender}, and so any transformations here will be scaled appropriately.
     * If you need to do pre-scale translations, use {@link #preRender}
	 */
	default void adjustRenderPose(R renderState, PoseStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {}

	/**
	 * Construct the {@link AnimationState} for the given render pass, ready to pass onto the {@link GeoModel} for handling
	 */
	default AnimationState<T> createAnimationState(R renderState) {
		return new AnimationState<>(renderState);
	}

	/**
	 * Sets a {@link GeoBone} as visible or hidden, with support for lazy variable passing
	 */
	default void setBonesVisible(boolean visible, String... boneNames) {
		GeoModel<T> model = getGeoModel();

		for (String boneName : boneNames) {
			model.getBone(boneName).ifPresent(bone -> bone.setHidden(!visible));
		}
	}

	/**
	 * Sets a {@link GeoBone} as visible or hidden, with support for lazy variable passing
	 */
	default void setBonesVisible(boolean visible, @Nullable GeoBone... bones) {
		if (bones == null)
			return;

		for (GeoBone bone : bones) {
			if (bone != null)
				bone.setHidden(!visible);
		}
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
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState);

	/**
	 * Internal helper method to help with type-resolution of the {@link DataTickets#PER_BONE_TASKS} DataTicket
	 */
    default Reference2ObjectMap<GeoBone, Pair<MutableObject<PoseStack.Pose>, PerBoneRender<R>>> getPerBoneTasks(R renderState) {
		return renderState.getGeckolibData(DataTickets.PER_BONE_TASKS);
	}
}
