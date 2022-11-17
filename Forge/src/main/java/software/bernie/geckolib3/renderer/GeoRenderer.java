package software.bernie.geckolib3.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.cache.object.*;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.core.object.Color;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
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
	 * Gets the {@link RenderType} to render the given animatable with.<br>
	 * Uses the {@link RenderType#entityCutoutNoCull}	{@code RenderType} by default.<br>
	 * Override this to change the way a model will render (such as translucent models, etc)
	 */
	default RenderType getRenderType(T animatable, ResourceLocation texture,
									 @Nullable MultiBufferSource bufferSource,
									 float partialTick) {
		return getGeoModel().getRenderType(animatable, texture);
	}

	/**
	 * Gets a tint-applying color to render the given animatable with.<br>
	 * Returns {@link Color#WHITE} by default
	 */
	default Color getRenderColor(PoseStack poseStack, T animatable, @Nullable MultiBufferSource bufferSource,
								 @Nullable VertexConsumer buffer, float partialTick, int packedLight) {
		return Color.WHITE;
	}

	/**
	 * Gets a packed overlay coordinate pair for rendering.<br>
	 * Mostly just used for the red tint when an entity is hurt,
	 * but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper}
	 * white tint when exploding.
	 */
	default int getPackedOverlay(T animatable, float u) {
		return OverlayTexture.NO_OVERLAY;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	default long getInstanceId(T animatable) {
		return animatable.hashCode();
	}

	// TODO do some testing to work out a better/more consistent way to approach this
	/**
	 * Determines the threshold value before the animatable should be considered moving for animation purposes.<br>
	 * The default value and usage for this varies depending on the renderer.<br>
	 * <ul>
	 *     <li>For entities, it represents how far (from 0) the arm swing should be moving before counting as moving for animation purposes.</li>
	 *     <li>For projectiles, it represents the average velocity of the object.</li>
	 *     <li>For Tile Entities, it's currently unused</li>
	 * </ul>
	 * The lower the value, the more sensitive the {@link AnimationEvent#isMoving()} check will be.<br>
	 * Particularly low values may have adverse effects however
	 */
	default float getMotionAnimThreshold(T animatable) {
		return 0.15f;
	}

	/**
	 * Initial access point for rendering. It all begins here.<br>
	 * All GeckoLib renderers should immediately defer their respective default {@code render} calls to this, for consistent handling
	 */
	default void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer,
							   float yaw, float partialTick, int packedLight) {
		poseStack.pushPose();

		Color renderColor = getRenderColor(poseStack, animatable, bufferSource, null, partialTick, packedLight);
		float red = renderColor.getRedFloat();
		float green = renderColor.getGreenFloat();
		float blue = renderColor.getBlueFloat();
		float alpha = renderColor.getAlphaFloat();
		int packedOverlay = getPackedOverlay(animatable, 0);
		BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));

		if (renderType == null)
			renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);

		if (buffer == null)
			buffer = bufferSource.getBuffer(renderType);

		preRender(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight,
				packedOverlay, red, green, blue, alpha);

		preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, packedLight, packedLight, packedOverlay);

		actuallyRender(poseStack, animatable, model, renderType,
				bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

		applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

		postRender(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight,
				packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}

	/**
	 * The actual render method that sub-type renderers should override to handle their specific rendering tasks.<br>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	default void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
								MultiBufferSource bufferSource, VertexConsumer buffer,
								float partialTick, int packedLight, int packedOverlay,
								float red, float green, float blue, float alpha) {
		for (GeoBone group : model.topLevelBones()) {
			renderRecursively(poseStack, group, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue,
					alpha);
		}
	}

	/**
	 * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#preRender pre-render} actions.
	 */
	default void preApplyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource,
								   VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
			renderLayer.preRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
		}
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	default void applyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource,
								   VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
			renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
		}
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory
	 * work such as scaling and translating.<br>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	default void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight,
						   int packedOverlay, float red, float green, float blue, float alpha) {}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	default void postRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
							float alpha) {}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	default void renderRecursively(PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
								   int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		RenderUtils.prepMatrixForBone(poseStack, bone);
		renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		renderChildBones(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	/**
	 * Renders the {@link GeoCube GeoCubes} associated with a given {@link GeoBone}
	 */
	default void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
								   int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isHidden())
			return;

		for (GeoCube cube : bone.getCubes()) {
			poseStack.pushPose();
			renderCube(poseStack, cube, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			poseStack.popPose();
		}
	}

	/**
	 * Render the child bones of a given {@link GeoBone}.<br>
	 * Note that this does not render the bone itself. That should be done through {@link GeoRenderer#renderCubesOfBone} separately
	 */
	default void renderChildBones(PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource, VertexConsumer buffer,
								  float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isHidingChildren())
			return;

		for (GeoBone childBone : bone.getChildBones()) {
			renderRecursively(poseStack, childBone, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	/**
	 * Renders an individual {@link GeoCube}.<br>
	 * This tends to be called recursively from something like {@link GeoRenderer#renderCubesOfBone}
	 */
	default void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight,
							int packedOverlay, float red, float green, float blue, float alpha) {
		RenderUtils.translateToPivotPoint(poseStack, cube);
		RenderUtils.rotateMatrixAroundCube(poseStack, cube);
		RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

		Matrix3f normalisedPoseState = poseStack.last().normal();
		Matrix4f poseState = poseStack.last().pose();

		for (GeoQuad quad : cube.quads()) {
			if (quad == null)
				continue;

			Vector3f normal = quad.normal().copy();

			normal.transform(normalisedPoseState);
			RenderUtils.fixInvertedFlatCube(cube, normal);
			createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}

	/**
	 * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link VertexConsumer buffer} for rendering
	 */
	default void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		for (GeoVertex vertex : quad.vertices()) {
			Vector3f position = vertex.position();
			Vector4f vector4f = new Vector4f(position.x(), position.y(), position.z(), 1);

			vector4f.transform(poseState);
			buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texU(),
					vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
		}
	}
}
