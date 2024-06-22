package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base interface for all GeckoLib renderers.<br>
 */
public interface GeoRenderer<T extends GeoAnimatable> {
	/**
	 * Gets the model instance for this renderer
	 */
	GeoModel<T> getGeoModel();

	/**
	 * Gets the {@link GeoAnimatable} instance currently being rendered
	 */
	T getAnimatable();

	/**
	 * Gets the texture resource location to render for the given animatable
	 */
	default ResourceLocation getTextureLocation(T animatable) {
		return getGeoModel().getTextureResource(animatable);
	}

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	default List<GeoRenderLayer<T>> getRenderLayers() {
		return List.of();
	}

	/**
	 * Gets the {@link RenderType} to render the given animatable with
	 * <p>
	 * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default
	 * <p>
	 * Override this to change the way a model will render (such as translucent models, etc)
	 *
	 * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions taking place
	 */
	@Nullable
	default RenderType getRenderType(T animatable, ResourceLocation texture,
									 @Nullable MultiBufferSource bufferSource,
									 float partialTick) {
		return getGeoModel().getRenderType(animatable, texture);
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns {@link Color#WHITE} by default
	 */
	default Color getRenderColor(T animatable, float partialTick, int packedLight) {
		return Color.WHITE;
	}

	/**
	 * Gets a packed overlay coordinate pair for rendering
	 * <p>
	 * Mostly just used for the red tint when an entity is hurt,
	 * but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper}
	 * white tint when exploding.
	 */
	default int getPackedOverlay(T animatable, float u, float partialTick) {
		return OverlayTexture.NO_OVERLAY;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes
	 * <p>
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	default long getInstanceId(T animatable) {
		return animatable.hashCode();
	}

	/**
	 * Determines the threshold value before the animatable should be considered moving for animation purposes
	 * <p>
	 * The default value and usage for this varies depending on the renderer
	 * <ul>
	 *     <li>For entities, it represents the averaged lateral velocity of the object.</li>
	 *     <li>For {@link software.bernie.geckolib.animatable.GeoBlockEntity Tile Entities} and {@link software.bernie.geckolib.animatable.GeoItem Items}, it's currently unused</li>
	 *</ul>
	 *
	 *
	 * The lower the value, the more sensitive the {@link AnimationState#isMoving()} check will be.<br>
	 * Particularly low values may have adverse effects however
	 */
	default float getMotionAnimThreshold(T animatable) {
		return 0.015f;
	}

	/**
	 * Initial access point for rendering. It all begins here
	 * <p>
	 * All GeckoLib renderers should immediately defer their respective default {@code render} calls to this, for consistent handling
	 */
	default void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer,
							   float yaw, float partialTick, int packedLight) {
		poseStack.pushPose();

		int renderColor = getRenderColor(animatable, partialTick, packedLight).argbInt();
		int packedOverlay = getPackedOverlay(animatable, 0, partialTick);
		BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));

		if (renderType == null)
			renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);

		if (buffer == null && renderType != null)
			buffer = bufferSource.getBuffer(renderType);

		preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);

		if (firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
			preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, packedLight, packedLight, packedOverlay);
			actuallyRender(poseStack, animatable, model, renderType,
					bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
			applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
			postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
			firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
		}

		poseStack.popPose();

		renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Re-renders the provided {@link BakedGeoModel} using the existing {@link GeoRenderer}
	 * <p>
	 * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering whilst inside a {@link GeoRenderLayer} or similar
	 */
	default void reRender(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, T animatable,
						  RenderType renderType, VertexConsumer buffer, float partialTick,
						  int packedLight, int packedOverlay, int colour) {
		poseStack.pushPose();
		preRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
		actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
		postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
		poseStack.popPose();
	}

	/**
	 * The actual render method that sub-type renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	default void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
								MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
								int packedLight, int packedOverlay, int colour) {
		if (buffer == null) {
			if (renderType == null)
				return;

			buffer = bufferSource.getBuffer(renderType);
		}

		updateAnimatedTextureFrame(animatable);

		for (GeoBone group : model.topLevelBones()) {
			renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
					packedOverlay, colour);
		}
	}

	/**
	 * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#preRender pre-render} actions.
	 */
	default void preApplyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource,
								   @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
			renderLayer.preRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
		}
	}

	/**
	 * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#renderForBone per-bone} render actions.
	 */
	default void applyRenderLayersForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
										  VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
			renderLayer.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
		}
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	default void applyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource,
								   @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
			renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
		}
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	default void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
						   int packedOverlay, int colour) {}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here
	 * <p>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	default void postRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {}

	/**
	 * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
	 * <p>
	 * This method is <u>not</u> called in {@link GeoRenderer#reRender re-render}
	 */
	default void renderFinal(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight,
							 int packedOverlay, int colour) {}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	default void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
								   VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
								   int packedOverlay, int colour) {
		poseStack.pushPose();
		RenderUtil.prepMatrixForBone(poseStack, bone);
		renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

		if (!isReRender) {
			applyRenderLayersForBone(poseStack, getAnimatable(), bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

			if (buffer instanceof BufferBuilder builder && !builder.building)
				buffer = bufferSource.getBuffer(renderType);
		}

		renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
		poseStack.popPose();
	}

	/**
	 * Renders the {@link GeoCube GeoCubes} associated with a given {@link GeoBone}
	 */
	default void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
								   int packedOverlay, int colour) {
		if (bone.isHidden())
			return;

		for (GeoCube cube : bone.getCubes()) {
			poseStack.pushPose();
			renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
			poseStack.popPose();
		}
	}

	/**
	 * Render the child bones of a given {@link GeoBone}
	 * <p>
	 * Note that this does not render the bone itself. That should be done through {@link GeoRenderer#renderCubesOfBone} separately
	 */
	default void renderChildBones(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
								  boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		if (bone.isHidingChildren())
			return;

		for (GeoBone childBone : bone.getChildBones()) {
			renderRecursively(poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
		}
	}

	/**
	 * Renders an individual {@link GeoCube}
	 * <p>
	 * This tends to be called recursively from something like {@link GeoRenderer#renderCubesOfBone}
	 */
	default void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight,
							int packedOverlay, int colour) {
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
			createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, colour);
		}
	}

	/**
	 * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link VertexConsumer buffer} for rendering
	 */
	default void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
									  int packedLight, int packedOverlay, int colour) {
		for (GeoVertex vertex : quad.vertices()) {
			Vector3f position = vertex.position();			
			Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

			buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), colour, vertex.texU(),
					vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
		}
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	void fireCompileRenderLayersEvent();

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight);
	
    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call
	 * <p>
     * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
     */
	default void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, T animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		if (!isReRender && (widthScale != 1 || heightScale != 1))
			poseStack.scale(widthScale, heightScale, widthScale);
	}

	/**
	 * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this GeoRenderer
	 * <p>
	 * This should only be called immediately prior to rendering
	 *
	 * @see AnimatableTexture#setAndUpdate
	 */
	void updateAnimatedTextureFrame(T animatable);
}
