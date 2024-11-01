package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.object.Color;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayersContainer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

/**
 * Base {@link GeoRenderer} class for rendering {@link Entity Entities} specifically
 * <p>
 * All entities added to be rendered by GeckoLib should use an instance of this class
 * <p>
 * This also includes {@link net.minecraft.world.entity.projectile.Projectile Projectiles}
 */
public class GeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T, EntityRenderState> implements GeoRenderer<T> {
	protected final GeoRenderLayersContainer<T> renderLayers = new GeoRenderLayersContainer<>(this);
	protected final GeoModel<T> model;

	protected T animatable;
	protected float partialTick;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f entityRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

	public GeoEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
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
	 * Getter for {@link EntityRenderer#reusedState} in case it is needed since GeoEntityRenderer doesn't actively pass it around
	 */
	public EntityRenderState getEntityRenderState() {
		return this.reusedState;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes
	 * <p>
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return animatable.getId();
	}

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	@Override
	public List<GeoRenderLayer<T>> getRenderLayers() {
		return this.renderLayers.getRenderLayers();
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoEntityRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.addLayer(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoEntityRenderer<T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoEntityRenderer<T> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

	/**
	 * Gets a tint-applying color to render the given animatable with
	 * <p>
	 * Returns {@link Color#WHITE} by default, modified for invisibility in spectator
	 */
	@Override
	public Color getRenderColor(T animatable, float partialTick, int packedLight) {
		Color color = GeoRenderer.super.getRenderColor(animatable, partialTick, packedLight);

		if (animatable.isInvisible() && !animatable.isInvisibleTo(ClientUtil.getClientPlayer()))
			color = Color.ofARGB(Mth.ceil(color.getAlpha() * 38 / 255f), color.getRed(), color.getGreen(), color.getBlue());

		return color;
	}

	/**
	 * Gets the {@link RenderType} to render the given animatable with
	 * <p>
	 * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default
	 * <p>
	 * Override this to change the way a model will render (such as translucent models, etc).
	 *
	 * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions taking place
	 */
	@Nullable
	@Override
	public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
		final boolean invisible = animatable.isInvisible();

		if (invisible && !animatable.isInvisibleTo(ClientUtil.getClientPlayer()))
			return RenderType.itemEntityTranslucentCull(texture);

		if (!invisible)
			return GeoRenderer.super.getRenderType(animatable, texture, bufferSource, partialTick);

		return Minecraft.getInstance().shouldEntityAppearGlowing(animatable) ? RenderType.outline(texture) : null;
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating
	 * <p>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int renderColor) {
		this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

		scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}

	@Override
	public void render(EntityRenderState entityRenderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		defaultRender(poseStack, this.animatable, bufferSource, null, null, this.partialTick, packedLight);
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks
	 * <p>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
							   MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
							   int packedLight, int packedOverlay, int renderColor) {
		poseStack.pushPose();

		LivingEntity livingEntity = animatable instanceof LivingEntity entity ? entity : null;
		boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);
		float lerpBodyRot = livingEntity == null ? 0 : Mth.rotLerp(partialTick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
		float lerpHeadRot = livingEntity == null ? 0 : Mth.rotLerp(partialTick, livingEntity.yHeadRotO, livingEntity.yHeadRot);
		float netHeadYaw = lerpHeadRot - lerpBodyRot;

		if (shouldSit && animatable.getVehicle() instanceof LivingEntity livingentity) {
			lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
			netHeadYaw = lerpHeadRot - lerpBodyRot;
			float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
			lerpBodyRot = lerpHeadRot - clampedHeadYaw;

			if (clampedHeadYaw * clampedHeadYaw > 2500f)
				lerpBodyRot += clampedHeadYaw * 0.2f;

			netHeadYaw = lerpHeadRot - lerpBodyRot;
		}

		if (animatable.getPose() == Pose.SLEEPING && livingEntity != null) {
			Direction bedDirection = livingEntity.getBedOrientation();

			if (bedDirection != null) {
				float eyePosOffset = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;

				poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
			}
		}

		float nativeScale = livingEntity != null ? livingEntity.getScale() : 1;
		float ageInTicks = animatable.tickCount + partialTick;
		float limbSwingAmount = 0;
		float limbSwing = 0;

		poseStack.scale(nativeScale, nativeScale, nativeScale);
		applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick, nativeScale);

		if (!shouldSit && animatable.isAlive() && livingEntity != null) {
			limbSwingAmount = livingEntity.walkAnimation.speed(partialTick);
			limbSwing = livingEntity.walkAnimation.position(partialTick);

			if (livingEntity.isBaby())
				limbSwing *= 3f;

			if (limbSwingAmount > 1f)
				limbSwingAmount = 1f;
		}

		if (!isReRender) {
			float motionThreshold = getMotionAnimThreshold(animatable);
			Vec3 velocity = animatable.getDeltaMovement();
			float avgVelocity = (float)((Math.abs(velocity.x) + Math.abs(velocity.z)) / 2f);
			float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
			long instanceId = getInstanceId(animatable);
			AnimationState<T> animationState = createAnimationState(animatable, instanceId, limbSwing, limbSwingAmount, partialTick, avgVelocity >= motionThreshold && limbSwingAmount != 0);

			animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, livingEntity != null && livingEntity.isBaby(), -netHeadYaw, -headPitch));
			getGeoModel().handleAnimations(animatable, instanceId, animationState, partialTick);
		}

		poseStack.translate(0, 0.01f, 0);

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (buffer != null)
			GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
					packedLight, packedOverlay, renderColor);

		poseStack.popPose();
	}

	/**
	 * Construct the {@link AnimationState} for the given render pass, ready to pass onto the {@link GeoModel} for handling.
	 * <p>
	 * Override this method to add additional {@link software.bernie.geckolib.constant.DataTickets data} to the AnimationState as needed
	 */
	@Override
	public AnimationState<T> createAnimationState(T animatable, long instanceId, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving) {
		AnimationState<T> animationState = GeoRenderer.super.createAnimationState(animatable, instanceId, limbSwing, limbSwingAmount, partialTick, isMoving);

		animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
		animationState.setData(DataTickets.ENTITY, animatable);

		return animationState;
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	@Override
	public void applyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable RenderType renderType,
                                  MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick,
                                  int packedLight, int packedOverlay, int renderColor) {
		if (!animatable.isSpectator())
			GeoRenderer.super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);
	}

	/**
	 * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state
	 * <p>
	 * This method is <u>not</u> called in {@link GeoRenderer#reRender re-render}
	 */
	@Override
	public void renderFinal(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int renderColor) {
		super.render(getEntityRenderState(), poseStack, bufferSource, packedLight);
	}

	/**
	 * Called after all render operations are completed and the render pass is considered functionally complete.
	 * <p>
	 * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render maintenance tasks as required
	 */
	@Override
	public void doPostRenderCleanup() {
		this.animatable = null;
		this.entityRenderTranslations = null;
		this.modelRenderTranslations = null;
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
								  int packedOverlay, int renderColor) {
		poseStack.pushPose();
		RenderUtil.translateMatrixToBone(poseStack, bone);
		RenderUtil.translateToPivotPoint(poseStack, bone);
		RenderUtil.rotateMatrixAroundBone(poseStack, bone);
		RenderUtil.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());
			Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

			bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			bone.setLocalSpaceMatrix(RenderUtil.translateMatrix(localMatrix, getRenderOffset(getEntityRenderState()).toVector3f()));
			bone.setWorldSpaceMatrix(RenderUtil.translateMatrix(new Matrix4f(localMatrix), this.animatable.position().toVector3f()));
		}

		RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

		buffer = checkAndRefreshBuffer(isReRender, buffer, bufferSource, renderType);

		renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, renderColor);

		if (!isReRender)
			applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);

		renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, renderColor);

		poseStack.popPose();
	}

	/**
	 * Applies rotation transformations to the renderer prior to render time to account for various entity states
	 */
	protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
		if (isShaking(animatable))
			rotationYaw += (float)(Math.cos(animatable.tickCount * 3.25d) * Math.PI * 0.4d);

		if (!animatable.hasPose(Pose.SLEEPING))
			poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));

		if (animatable instanceof LivingEntity livingEntity) {
			if (livingEntity.deathTime > 0) {
				float deathRotation = (livingEntity.deathTime + partialTick - 1f) / 20f * 1.6f;

				poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable, partialTick)));
			}
			else if (livingEntity.isAutoSpinAttack()) {
				poseStack.mulPose(Axis.XP.rotationDegrees(-90f - livingEntity.getXRot()));
				poseStack.mulPose(Axis.YP.rotationDegrees(ageInTicks * -75f));
			}
			else if (animatable.hasPose(Pose.SLEEPING)) {
				Direction bedOrientation = livingEntity.getBedOrientation();

				poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw));
				poseStack.mulPose(Axis.ZP.rotationDegrees(getDeathMaxRotation(animatable, partialTick)));
				poseStack.mulPose(Axis.YP.rotationDegrees(270f));
			}
			else if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
				poseStack.translate(0, (animatable.getBbHeight() + 0.1f) / nativeScale, 0);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
			}
		}
	}

	/**
	 * Gets the max rotation value for dying entities
	 * <p>
	 * You might want to modify this for different aesthetics, such as a {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death
	 * <p>
	 * Functionally equivalent to {@link LivingEntityRenderer#getFlipDegrees()}
	 */
	protected float getDeathMaxRotation(T animatable, float partialTick) {
		return 90f;
	}

	/**
	 * Get the maximum distance (in blocks) that an entity's nameplate should be visible when it is sneaking
	 * <p>
	 * This is only a short-circuit predicate, and other conditions after this check must be also passed in order for the name to render
	 */
	public double getNameRenderCutoffDistance(T animatable) {
		return 32d;
	}

	/**
	 * Whether the entity's nametag should be rendered or not
	 * <p>
	 * Pretty much exclusively used in {@link EntityRenderer#renderNameTag}
	 */
	@Override
	public boolean shouldShowName(T animatable, double distToCameraSq) {
		if (!(animatable instanceof LivingEntity))
			return super.shouldShowName(animatable, distToCameraSq);

		if (animatable.isDiscrete()) {
			double nameRenderCutoff = getNameRenderCutoffDistance(animatable);

			if (distToCameraSq >= nameRenderCutoff * nameRenderCutoff)
				return false;
		}

		if (animatable instanceof Mob && (!animatable.shouldShowName() && (!animatable.hasCustomName() || animatable != this.entityRenderDispatcher.crosshairPickEntity)))
			return false;

		final Minecraft minecraft = Minecraft.getInstance();
		boolean visibleToClient = !animatable.isInvisibleTo(minecraft.player);
		Team entityTeam = animatable.getTeam();

		if (entityTeam == null)
			return Minecraft.renderNames() && animatable != minecraft.getCameraEntity() && visibleToClient && !animatable.isVehicle();

		Team playerTeam = minecraft.player.getTeam();

		return switch (entityTeam.getNameTagVisibility()) {
			case ALWAYS -> visibleToClient;
			case NEVER -> false;
			case HIDE_FOR_OTHER_TEAMS -> playerTeam == null ? visibleToClient : entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || visibleToClient);
			case HIDE_FOR_OWN_TEAM -> playerTeam == null ? visibleToClient : !entityTeam.isAlliedTo(playerTeam) && visibleToClient;
		};
	}

	/**
	 * Gets a packed overlay coordinate pair for rendering
	 * <p>
	 * Mostly just used for the red tint when an entity is hurt,
	 * but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper}
	 * white tint when exploding.
	 */
	@Override
	public int getPackedOverlay(T animatable, float u, float partialTick) {
		if (!(animatable instanceof LivingEntity entity))
			return OverlayTexture.NO_OVERLAY;

		return OverlayTexture.pack(OverlayTexture.u(u),
				OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	/**
	 * Whether the entity is currently shaking. This is usually used for freezing, but also for things like piglin conversion or striders suffocating
	 * <p>
	 * This is used for a shaking effect while rendering
	 *
	 * @see LivingEntityRenderer#isShaking(LivingEntityRenderState)
	 */
	public boolean isShaking(T animatable) {
		return animatable.isFullyFrozen();
	}

	/**
	 * Create the EntityRenderState for vanilla.
	 * <p>
	 * GeckoLib defers creation of this to allow for dynamic handling in {@link #extractRenderState(Entity, EntityRenderState, float)}
	 * <p>
	 * This shouldn't actually be used for anything, so should be safe to ignore
	 */
	@ApiStatus.Internal
	@Nullable
	@Override
	public EntityRenderState createRenderState() {
		return null;
	}

	/**
	 * Create the EntityRenderState for vanilla.
	 * <p>
	 * GeckoLib defers creation of this to allow for dynamic handling in {@link #extractRenderState(Entity, EntityRenderState, float)}
	 * <p>
	 * This shouldn't actually be used for anything, so should be safe to ignore
	 */
	@ApiStatus.Internal
	@Override
	public final EntityRenderState createRenderState(T entity, float partialTick) {
		this.animatable = entity;
		this.partialTick = partialTick;

		if (this.reusedState == null)
			this.reusedState = entity instanceof LivingEntity ? new LivingEntityRenderState() : new EntityRenderState();

		extractRenderState(entity, this.reusedState, partialTick);

		return this.reusedState;
	}

	/**
	 * Fill the EntityRenderState for vanilla for the current render pass.
	 * <p>
	 * This shouldn't actually be used for anything, so should be safe to ignore
	 */
	@ApiStatus.Internal
	@Override
	public void extractRenderState(T entity, @Nullable EntityRenderState entityRenderState, float partialTick) {
		super.extractRenderState(entity, entityRenderState, partialTick);

		if (entityRenderState instanceof LivingEntityRenderState livingEntityRenderState)
			RenderUtil.prepLivingEntityRenderState((LivingEntity)entity, livingEntityRenderState, partialTick);
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeckoLibServices.Client.EVENTS.fireCompileEntityRenderLayers(this);
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer
	 *
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		return GeckoLibServices.Client.EVENTS.fireEntityPreRender(this, poseStack, model, bufferSource, partialTick, packedLight);
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		GeckoLibServices.Client.EVENTS.fireEntityPostRender(this, poseStack, model, bufferSource, partialTick, packedLight);
	}
}
