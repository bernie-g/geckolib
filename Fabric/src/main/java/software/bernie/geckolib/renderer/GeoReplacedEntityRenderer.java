package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.List;

/**
 * An alternate to {@link GeoEntityRenderer}, used specifically for replacing existing non-geckolib
 * entities with geckolib rendering dynamically, without the need for an additional entity class
 */
public class GeoReplacedEntityRenderer<E extends Entity, T extends GeoAnimatable> extends EntityRenderer<E> implements GeoRenderer<T> {
	protected final GeoModel<T> model;
	protected final List<GeoRenderLayer<T>> renderLayers = new ObjectArrayList<>();
	protected final T animatable;

	protected E currentEntity;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f entityRenderTranslations = new Matrix4f();
	protected Matrix4f modelRenderTranslations = new Matrix4f();

	public GeoReplacedEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, T animatable) {
		super(renderManager);

		this.model = model;
		this.animatable = animatable;
		
		fireCompileRenderLayersEvent();
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
	 * @see GeoReplacedEntityRenderer#getCurrentEntity()
	 */
	@Override
	public T getAnimatable() {
		return this.animatable;
	}

	/**
	 * Returns the current entity having its rendering replaced by this renderer
	 * @see GeoReplacedEntityRenderer#getAnimatable()
	 */
	public E getCurrentEntity() {
		return this.currentEntity;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return this.currentEntity.getId();
	}

	/**
	 * Shadowing override of {@link EntityRenderer#getTextureLocation}.<br>
	 * This redirects the call to {@link GeoRenderer#getTextureLocation}
	 */
	@Override
	public ResourceLocation getTextureLocation(E entity) {
		return GeoRenderer.super.getTextureLocation(this.animatable);
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
	public GeoReplacedEntityRenderer<E, T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.add(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoReplacedEntityRenderer<E, T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoReplacedEntityRenderer<E, T> withScale(float scaleWidth, float scaleHeight) {
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
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
						  float alpha) {
		this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (this.scaleWidth != 1 && this.scaleHeight != 1)
			poseStack.scale(this.scaleWidth, this.scaleHeight, this.scaleWidth);
	}

	@Override
	public void render(E entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		this.currentEntity = entity;

		defaultRender(poseStack, this.animatable, bufferSource, null, null, entityYaw, partialTick, packedLight);
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();

		LivingEntity livingEntity = this.currentEntity instanceof LivingEntity entity ? entity : null;

		if (this.currentEntity instanceof Mob mob && !isReRender) {
			Entity leashHolder = mob.getLeashHolder();

			if (leashHolder != null)
				renderLeash(mob, partialTick, poseStack, bufferSource, leashHolder);
		}

		boolean shouldSit = this.currentEntity.isPassenger() && (this.currentEntity.getVehicle() != null);
		float lerpBodyRot = livingEntity == null ? 0 : Mth.rotLerp(partialTick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
		float lerpHeadRot = livingEntity == null ? 0 : Mth.rotLerp(partialTick, livingEntity.yHeadRotO, livingEntity.yHeadRot);
		float netHeadYaw = lerpHeadRot - lerpBodyRot;

		if (shouldSit && this.currentEntity.getVehicle() instanceof LivingEntity livingentity) {
			lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
			netHeadYaw = lerpHeadRot - lerpBodyRot;
			float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
			lerpBodyRot = lerpHeadRot - clampedHeadYaw;

			if (clampedHeadYaw * clampedHeadYaw > 2500f)
				lerpBodyRot += clampedHeadYaw * 0.2f;

			netHeadYaw = lerpHeadRot - lerpBodyRot;
		}

		if (this.currentEntity.getPose() == Pose.SLEEPING && livingEntity != null) {
			Direction bedDirection = livingEntity.getBedOrientation();

			if (bedDirection != null) {
				float eyePosOffset = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;

				poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
			}
		}

		float ageInTicks = this.currentEntity.tickCount + partialTick;
		float limbSwingAmount = 0;
		float limbSwing = 0;

		applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

		if (!shouldSit && this.currentEntity.isAlive() && livingEntity != null) {
			limbSwingAmount = livingEntity.walkAnimation.speed(partialTick);
			limbSwing = livingEntity.walkAnimation.position(partialTick);

			if (livingEntity.isBaby())
				limbSwing *= 3f;

			if (limbSwingAmount > 1f)
				limbSwingAmount = 1f;
		}

		float headPitch = Mth.lerp(partialTick, this.currentEntity.xRotO, this.currentEntity.getXRot());float motionThreshold = getMotionAnimThreshold(animatable);
		boolean isMoving;

		if (livingEntity != null) {
			Vec3 velocity = livingEntity.getDeltaMovement();
			float avgVelocity = (float)(Math.abs(velocity.x) + Math.abs(velocity.z)) / 2f;

			isMoving = avgVelocity >= motionThreshold && limbSwingAmount != 0;
		}
		else {
			isMoving = (limbSwingAmount <= -motionThreshold || limbSwingAmount >= motionThreshold);
		}

		if (!isReRender) {
			AnimationState<T> animationState = new AnimationState<T>(animatable, limbSwing, limbSwingAmount, partialTick, isMoving);
			long instanceId = getInstanceId(animatable);

			animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
			animationState.setData(DataTickets.ENTITY, this.currentEntity);
			animationState.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(shouldSit, livingEntity != null && livingEntity.isBaby(), -netHeadYaw, -headPitch));
			this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);
			this.model.handleAnimations(animatable, instanceId, animationState);
		}

		poseStack.translate(0, 0.01f, 0);
		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

		this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

		if (!this.currentEntity.isInvisibleTo(Minecraft.getInstance().player))
			GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}

	/**
	 * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
	 */
	@Override
	public void applyRenderLayers(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		if (!this.currentEntity.isSpectator())
			GeoRenderer.super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
	}

	/**
	 * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
	 * {@link PoseStack} transformations will be unused and lost once this method ends
	 */
	@Override
	public void postRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (!isReRender)
			super.render(this.currentEntity, 0, partialTick, poseStack, bufferSource, packedLight);
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
								  int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		RenderUtils.translateMatrixToBone(poseStack, bone);
		RenderUtils.translateToPivotPoint(poseStack, bone);
		RenderUtils.rotateMatrixAroundBone(poseStack, bone);
		RenderUtils.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingMatrices()) {
			Matrix4f poseState = new Matrix4f(poseStack.last().pose());
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

			bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
			localMatrix.translate(new Vector3f(getRenderOffset(this.currentEntity, 1).toVector3f()));
			bone.setLocalSpaceMatrix(localMatrix);

			Matrix4f worldState = localMatrix;

			worldState.translate(new Vector3f(this.currentEntity.position().toVector3f()));
			bone.setWorldSpaceMatrix(worldState);
		}

		RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

		renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		if (!isReRender)
			applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

		renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

		poseStack.popPose();
	}

	/**
	 * Applies rotation transformations to the renderer prior to render time to account for various entity states
	 */
	protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
								  float partialTick) {
		Pose pose = this.currentEntity.getPose();
		LivingEntity livingEntity = this.currentEntity instanceof LivingEntity entity ? entity : null;

		if (pose != Pose.SLEEPING)
			poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));

		if (livingEntity != null && livingEntity.deathTime > 0) {
			float deathRotation = (livingEntity.deathTime + partialTick - 1f) / 20f * 1.6f;

			poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
		}
		else if (livingEntity != null && livingEntity.isAutoSpinAttack()) {
			poseStack.mulPose(Axis.XP.rotationDegrees(-90f - livingEntity.getXRot()));
			poseStack.mulPose(Axis.YP.rotationDegrees((livingEntity.tickCount + partialTick) * -75f));
		}
		else if (livingEntity != null && pose == Pose.SLEEPING) {
			Direction bedOrientation = livingEntity.getBedOrientation();

			poseStack.mulPose(Axis.YP.rotationDegrees(bedOrientation != null ? RenderUtils.getDirectionAngle(bedOrientation) : rotationYaw));
			poseStack.mulPose(Axis.ZP.rotationDegrees(getDeathMaxRotation(animatable)));
			poseStack.mulPose(Axis.YP.rotationDegrees(270f));
		}
		else if (this.currentEntity.hasCustomName() || this.currentEntity instanceof Player) {
			String name = this.currentEntity.getName().getString();

			if (this.currentEntity instanceof Player player) {
				if (!player.isModelPartShown(PlayerModelPart.CAPE))
					return;
			}
			else {
				name = ChatFormatting.stripFormatting(name);
			}

			if (name != null && (name.equals("Dinnerbone") || name.equalsIgnoreCase("Grumm"))) {
				poseStack.translate(0, this.currentEntity.getBbHeight() + 0.1f, 0);
				poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
			}
		}
	}

	/**
	 * Gets the max rotation value for dying entities.<br>
	 * You might want to modify this for different aesthetics, such as a {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death.<br>
	 * Functionally equivalent to {@link net.minecraft.client.renderer.entity.LivingEntityRenderer#getFlipDegrees}
	 */
	protected float getDeathMaxRotation(T animatable) {
		return 90f;
	}

	/**
	 * Whether the entity's nametag should be rendered or not.<br>
	 * Pretty much exclusively used in {@link EntityRenderer#renderNameTag}
	 */
	@Override
	public boolean shouldShowName(E entity) {
		double nameRenderDistance = this.currentEntity.isDiscrete() ? 32d : 64d;

		if (this.entityRenderDispatcher.distanceToSqr(this.currentEntity) >= nameRenderDistance * nameRenderDistance)
			return false;

		return this.currentEntity == this.entityRenderDispatcher.crosshairPickEntity && this.currentEntity.hasCustomName() && Minecraft.renderNames();
	}

	/**
	 * Gets a packed overlay coordinate pair for rendering.<br>
	 * Mostly just used for the red tint when an entity is hurt,
	 * but can be used for other things like the {@link net.minecraft.world.entity.monster.Creeper}
	 * white tint when exploding.
	 */
	@Override
	public int getPackedOverlay(T animatable, float u) {
		if (!(this.currentEntity instanceof LivingEntity entity))
			return OverlayTexture.NO_OVERLAY;

		return OverlayTexture.pack(OverlayTexture.u(u),
				OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	/**
	 * Static rendering code for rendering a leash segment.<br>
	 * It's a like-for-like from {@link net.minecraft.client.renderer.entity.MobRenderer#renderLeash} that had to be duplicated here for flexible usage
	 */
	public <H extends Entity, M extends Mob> void renderLeash(M mob, float partialTick, PoseStack poseStack,
															  MultiBufferSource bufferSource, H leashHolder) {
		double lerpBodyAngle = (Mth.lerp(partialTick, mob.yBodyRot, mob.yBodyRotO) * Mth.DEG_TO_RAD) + Mth.HALF_PI;
		Vec3 leashOffset = mob.getLeashOffset();
		double xAngleOffset = Math.cos(lerpBodyAngle) * leashOffset.z + Math.sin(lerpBodyAngle) * leashOffset.x;
		double zAngleOffset = Math.sin(lerpBodyAngle) * leashOffset.z - Math.cos(lerpBodyAngle) * leashOffset.x;
		double lerpOriginX = Mth.lerp(partialTick, mob.xo, mob.getX()) + xAngleOffset;
		double lerpOriginY = Mth.lerp(partialTick, mob.yo, mob.getY()) + leashOffset.y;
		double lerpOriginZ = Mth.lerp(partialTick, mob.zo, mob.getZ()) + zAngleOffset;
		Vec3 ropeGripPosition = leashHolder.getRopeHoldPosition(partialTick);
		float xDif = (float)(ropeGripPosition.x - lerpOriginX);
		float yDif = (float)(ropeGripPosition.y - lerpOriginY);
		float zDif = (float)(ropeGripPosition.z - lerpOriginZ);
		float offsetMod = Mth.invSqrt(xDif * xDif + zDif * zDif) * 0.025f / 2f;
		float xOffset = zDif * offsetMod;
		float zOffset = xDif * offsetMod;
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		BlockPos entityEyePos = BlockPos.containing(mob.getEyePosition(partialTick));
		BlockPos holderEyePos = BlockPos.containing(leashHolder.getEyePosition(partialTick));
		int entityBlockLight = getBlockLightLevel((E)mob, entityEyePos);
		int holderBlockLight = leashHolder.isOnFire() ? 15 : leashHolder.level.getBrightness(LightLayer.BLOCK, holderEyePos);
		int entitySkyLight = mob.level.getBrightness(LightLayer.SKY, entityEyePos);
		int holderSkyLight = mob.level.getBrightness(LightLayer.SKY, holderEyePos);

		poseStack.pushPose();
		poseStack.translate(xAngleOffset, leashOffset.y, zAngleOffset);

		Matrix4f posMatrix = new Matrix4f(poseStack.last().pose());

		for (int segment = 0; segment <= 24; ++segment) {
			renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.025f, xOffset, zOffset, segment, false);
		}

		for (int segment = 24; segment >= 0; --segment) {
			renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.0f, xOffset, zOffset, segment, true);
		}

		poseStack.popPose();
	}

	/**
	 * Static rendering code for rendering a leash segment.<br>
	 * It's a like-for-like from {@link net.minecraft.client.renderer.entity.MobRenderer#addVertexPair} that had to be duplicated here for flexible usage
	 */
	private static void renderLeashPiece(VertexConsumer buffer, Matrix4f positionMatrix, float xDif, float yDif,
										 float zDif, int entityBlockLight, int holderBlockLight, int entitySkyLight,
										 int holderSkyLight, float width, float yOffset, float xOffset, float zOffset, int segment, boolean isLeashKnot) {
		float piecePosPercent = segment / 24f;
		int lerpBlockLight = (int)Mth.lerp(piecePosPercent, entityBlockLight, holderBlockLight);
		int lerpSkyLight = (int)Mth.lerp(piecePosPercent, entitySkyLight, holderSkyLight);
		int packedLight = LightTexture.pack(lerpBlockLight, lerpSkyLight);
		float knotColourMod = segment % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1f;
		float red = 0.5f * knotColourMod;
		float green = 0.4f * knotColourMod;
		float blue = 0.3f * knotColourMod;
		float x = xDif * piecePosPercent;
		float y = yDif > 0.0f ? yDif * piecePosPercent * piecePosPercent : yDif - yDif * (1.0f - piecePosPercent) * (1.0f - piecePosPercent);
		float z = zDif * piecePosPercent;

		buffer.vertex(positionMatrix, x - xOffset, y + yOffset, z + zOffset).color(red, green, blue, 1).uv2(packedLight).endVertex();
		buffer.vertex(positionMatrix, x + xOffset, y + width - yOffset, z - zOffset).color(red, green, blue, 1).uv2(packedLight).endVertex();
	}

	/**
	 * Create and fire the relevant {@code CompileLayers} event hook for this renderer
	 */
	@Override
	public void fireCompileRenderLayersEvent() {
		GeoRenderEvent.ReplacedEntity.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.CompileRenderLayers(this));
	}

	/**
	 * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
	 * @return Whether the renderer should proceed based on the cancellation state of the event
	 */
	@Override
	public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		return GeoRenderEvent.ReplacedEntity.Pre.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.Pre(this, poseStack, model, bufferSource, partialTick, packedLight));
	}

	/**
	 * Create and fire the relevant {@code Post-Render} event hook for this renderer
	 */
	@Override
	public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
		GeoRenderEvent.ReplacedEntity.Post.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.Post(this, poseStack, model, bufferSource, partialTick, packedLight));
	}
}
