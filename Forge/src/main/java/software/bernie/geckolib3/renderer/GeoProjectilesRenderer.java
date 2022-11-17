package software.bernie.geckolib3.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;

public class GeoProjectilesRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {
	protected final GeoModel<T> model;
	protected final List<GeoRenderLayer<T>> renderLayers = new ObjectArrayList<>();

	protected T animatable;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f renderStartPose = new Matrix4f();
	protected Matrix4f preRenderPose = new Matrix4f();

	public GeoProjectilesRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
		super(renderManager);

		this.model = model;
	}

	/**
	 * Gets the model instance for this renderer
	 */
	@Override
	public GeoModel<T> getGeoModel() {
		return this.model;
	}

	/**
	 * Gets the {@link GeoAnimatable} instance currently being rendered
	 */
	@Override
	public T getAnimatable() {
		return this.animatable;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return animatable.getId();
	}

	/**
	 * Shadowing override of {@link EntityRenderer#getTextureLocation}.<br>
	 * This redirects the call to {@link GeoRenderer#getTextureLocation}
	 */
	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return GeoRenderer.super.getTextureLocation(animatable);
	}

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	@Override
	public List<GeoRenderLayer<T>> getRenderLayers() {
		return this.renderLayers;
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoProjectilesRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.add(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoProjectilesRenderer<T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoProjectilesRenderer<T> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory
	 * work such as scaling and translating.<br>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
						  float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
						  float alpha) {
		this.preRenderPose = poseStack.last().pose().copy();

		if (this.scaleWidth != 1 && this.scaleHeight != 1)
			poseStack.scale(this.scaleWidth, this.scaleHeight, this.scaleWidth);

		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
	}

	@Override
	public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight) {
		this.animatable = entity;

		defaultRender(poseStack, entity, bufferSource, null, null, entityYaw, partialTick, packedLight);
	}

	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
							   MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
							   int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		float motionThreshold = getMotionAnimThreshold(animatable);
		Vec3 velocity = animatable.getDeltaMovement();
		float avgVelocity = ((float)Math.abs(velocity.x) + (float)Math.abs(velocity.y) + (float)Math.abs(velocity.z) / 3f);
		AnimationEvent<T> animationEvent = new AnimationEvent<>(animatable, 0, 0, partialTick, avgVelocity >= motionThreshold);

		this.model.setCustomAnimations(animatable, getInstanceId(animatable), animationEvent);

		if (!animatable.isInvisibleTo(Minecraft.getInstance().player))
			GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	@Override
	public void applyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
								  MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
								  int packedLight, int packedOverlay) {
		if (!animatable.isSpectator())
			GeoRenderer.super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
	}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	@Override
	public void postRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.render(animatable, 0, partialTick, poseStack, bufferSource, packedLight);
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
								  int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.renderStartPose);

			bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.preRenderPose));
			localMatrix.translate(new Vector3f(getRenderOffset(this.animatable, 1)));
			bone.setLocalSpaceMatrix(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(this.animatable.position()));
			bone.setWorldSpaceMatrix(worldState);
		}

		GeoRenderer.super.renderRecursively(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	/**
	 * Whether the entity's nametag should be rendered or not.<br>
	 * Pretty much exclusively used in {@link EntityRenderer#renderNameTag}
	 */
	@Override
	public boolean shouldShowName(T animatable) {
		double nameRenderDistance = animatable.isDiscrete() ? 32d : 64d;

		if (this.entityRenderDispatcher.distanceToSqr(animatable) >= nameRenderDistance * nameRenderDistance)
			return false;

		return animatable == this.entityRenderDispatcher.crosshairPickEntity && animatable.hasCustomName() && Minecraft.renderNames();
	}
}
