package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.WalkAnimationState;
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
	 */
	@ApiStatus.OverrideOnly
	default void addRenderData(T animatable, O relatedObject, R renderState) {}

	/**
	 * Internal method for capturing the common RenderState data for all animatable objects
	 */
	@ApiStatus.Internal
	default R captureDefaultRenderState(T animatable, O relatedObject, R renderState, float partialTick) {
		renderState.addGeckolibData(DataTickets.TICK, animatable.getTick(relatedObject));
		renderState.addGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, getInstanceId(animatable, relatedObject));
		renderState.addGeckolibData(DataTickets.ANIMATABLE_MANAGER, animatable.getAnimatableInstanceCache().getManagerForId(renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID)));
		renderState.addGeckolibData(DataTickets.PARTIAL_TICK, partialTick);
		renderState.addGeckolibData(DataTickets.RENDER_COLOR, getRenderColor(animatable, relatedObject, partialTick));
		renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, getPackedOverlay(animatable, relatedObject, 0, partialTick));
		renderState.addGeckolibData(DataTickets.IS_MOVING, false);
		renderState.addGeckolibData(DataTickets.BONE_RESET_TIME, animatable.getBoneResetTime());
		renderState.addGeckolibData(DataTickets.ANIMATABLE_CLASS, animatable.getClass());
		renderState.addGeckolibData(DataTickets.PER_BONE_TASKS, new ObjectArrayList<>(0));
		getGeoModel().prepareForRenderPass(animatable, renderState);

		return renderState;
	}

	/**
	 * Create the {@link GeoRenderState} for the upcoming render pass
	 * <p>
	 * This is an internal method only; you should <b><u>NOT</u></b> be overriding this<br>
	 * Override {@link #addRenderData(GeoAnimatable, Object, GeoRenderState)} instead
	 */
	@ApiStatus.Internal
	default R fillRenderState(T animatable, O relatedObject, R renderState, float partialTick) {
		captureDefaultRenderState(animatable, relatedObject, renderState, partialTick);
		addRenderData(animatable, relatedObject, renderState);

		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			renderLayer.addRenderData(animatable, relatedObject, renderState);
		}

		fireCompileRenderStateEvent(animatable, relatedObject, renderState);

		return renderState;
	}

	/**
	 * Initial access point for rendering; it all begins here.<br>
	 * The {@link GeoRenderState} should have already been filled by this stage.
	 * <p>
	 * All GeckoLib renderers should immediately defer their respective default {@code render} calls to this, for consistent handling
	 */
	default void defaultRender(R renderState, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer) {
		poseStack.pushPose();

		if (renderType == null)
			renderType = getRenderType(renderState, getTextureLocation(renderState));

		if (buffer == null && renderType != null)
			buffer = bufferSource.getBuffer(renderType);

		final GeoModel<T> geoModel = getGeoModel();
		final BakedGeoModel model = geoModel.getBakedModel(geoModel.getModelResource(renderState));
		final int packedOverlay = renderState.getGeckolibData(DataTickets.PACKED_OVERLAY);
		final int packedLight = renderState.getGeckolibData(DataTickets.PACKED_LIGHT);
		final int renderColor = renderState.getGeckolibData(DataTickets.RENDER_COLOR);

		preRender(renderState, poseStack, model, bufferSource, buffer, false, packedLight, packedOverlay, renderColor);

		if (firePreRenderEvent(renderState, poseStack, model, bufferSource)) {
			preApplyRenderLayers(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);
			actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, false, packedLight, packedOverlay, renderColor);
			applyRenderLayers(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);
			postRender(renderState, poseStack, model, bufferSource, buffer, false, packedLight, packedOverlay, renderColor);
			firePostRenderEvent(renderState, poseStack, model, bufferSource);
		}

		poseStack.popPose();

		renderFinal(renderState, poseStack, model, bufferSource, buffer);
		doPostRenderCleanup();
	}

	/**
	 * Re-renders the provided {@link BakedGeoModel} using the existing {@link GeoRenderer}
	 * <p>
	 * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering whilst inside a {@link GeoRenderLayer} or similar
	 */
	default void reRender(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, RenderType renderType, VertexConsumer buffer,
						  int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		preRender(renderState, poseStack, model, bufferSource, buffer, true, packedLight, packedOverlay, renderColor);
		actuallyRender(renderState, poseStack, model, renderType, bufferSource, buffer, true, packedLight, packedOverlay, renderColor);
		postRender(renderState, poseStack, model, bufferSource, buffer, true, packedLight, packedOverlay, renderColor);
		poseStack.popPose();
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	default void actuallyRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType,
								MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (renderType == null || buffer == null)
			return;

		for (GeoBone group : model.topLevelBones()) {
			renderRecursively(renderState, poseStack, group, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
		}
	}

	/**
	 * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#preRender pre-render} actions.
	 */
	default void preApplyRenderLayers(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			renderLayer.preRender(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);
		}
	}

	/**
	 * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#createPerBoneRender per-bone} render actions.
	 */
	default void applyRenderLayersForBone(R renderState, GeoBone bone, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource) {
		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			Runnable boneTask = renderLayer.createPerBoneRender(renderState, poseStack, bone, renderType, bufferSource);

			if (boneTask != null)
				renderState.getGeckolibData(DataTickets.PER_BONE_TASKS).add(Pair.of(poseStack.last().copy(), boneTask));
		}
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	default void applyRenderLayers(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
								   int packedLight, int packedOverlay, int renderColor) {
		for (Pair<PoseStack.Pose, Runnable> perBoneTask : (ObjectArrayList<Pair<PoseStack.Pose, Runnable>>)renderState.getGeckolibData(DataTickets.PER_BONE_TASKS)) {
			poseStack.pushPose();
			poseStack.last().set(perBoneTask.left());
			perBoneTask.right().run();
			poseStack.popPose();
		}

		for (GeoRenderLayer<T, O, R> renderLayer : getRenderLayers()) {
			renderLayer.render(renderState, poseStack, model, renderType, bufferSource, buffer, packedLight, packedOverlay, renderColor);
		}
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	default void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here
	 * <p>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	default void postRender(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {}

	/**
	 * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
	 * <p>
	 * This method is <u>not</u> called in {@link GeoRenderer#reRender re-render}
	 */
	default void renderFinal(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer) {}

	/**
	 * Called after all render operations are completed and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	default void doPostRenderCleanup() {}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	default void renderRecursively(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
								   boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();
		RenderUtil.prepMatrixForBone(poseStack, bone);

		renderCubesOfBone(renderState, bone, poseStack, buffer, packedLight, packedOverlay, renderColor);

		if (!isReRender)
			applyRenderLayersForBone(renderState, bone, poseStack, renderType, bufferSource);

		renderChildBones(renderState, bone, poseStack, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
		poseStack.popPose();
	}

	/**
	 * Renders the {@link GeoCube GeoCubes} associated with a given {@link GeoBone}
	 */
	default void renderCubesOfBone(R renderState, GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		if (bone.isHidden())
			return;

		for (GeoCube cube : bone.getCubes()) {
			poseStack.pushPose();
			renderCube(renderState, cube, poseStack, buffer, packedLight, packedOverlay, renderColor);
			poseStack.popPose();
		}
	}

	/**
	 * Render the child bones of a given {@link GeoBone}
	 * <p>
	 * Note that this does not render the bone itself. That should be done through {@link GeoRenderer#renderCubesOfBone} separately
	 */
	default void renderChildBones(R renderState, GeoBone bone, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
								  boolean isReRender, int packedLight, int packedColor, int renderColor) {
		if (bone.isHidingChildren())
			return;

		for (GeoBone childBone : bone.getChildBones()) {
			renderRecursively(renderState, poseStack, childBone, renderType, bufferSource, buffer, isReRender, packedLight, packedColor, renderColor);
		}
	}

	/**
	 * Renders an individual {@link GeoCube}
	 * <p>
	 * This tends to be called recursively from something like {@link GeoRenderer#renderCubesOfBone}
	 */
	default void renderCube(R renderState, GeoCube cube, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
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
	 * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
	 */
	default void scaleModelForRender(R renderState, float widthScale, float heightScale, PoseStack poseStack, BakedGeoModel model, boolean isReRender) {
		if (!isReRender && (widthScale != 1 || heightScale != 1))
			poseStack.scale(widthScale, heightScale, widthScale);
	}

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
	void fireCompileRenderStateEvent(T animatable, O relatedObject, R renderState);

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	boolean firePreRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	void firePostRenderEvent(R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource);
}
