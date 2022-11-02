package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LightType;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public abstract class GeoReplacedEntityRenderer<T extends IAnimatable> extends EntityRenderer implements IGeoRenderer {
	protected static final Map<Class<? extends IAnimatable>, GeoReplacedEntityRenderer> renderers = new ConcurrentHashMap<>();

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			GeoReplacedEntityRenderer renderer = renderers.get(object.getClass());
			return renderer == null ? null : renderer.getGeoModelProvider();
		});
	}

	protected final AnimatedGeoModel<IAnimatable> modelProvider;
	protected T animatable;
	protected final List<GeoLayerRenderer> layerRenderers = new ObjectArrayList<>();
	protected IAnimatable currentAnimatable;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected VertexConsumerProvider rtb = null;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoReplacedEntityRenderer(EntityRendererFactory.Context renderManager,
			AnimatedGeoModel<IAnimatable> modelProvider, T animatable) {
		super(renderManager);

		this.modelProvider = modelProvider;
		this.animatable = animatable;

		renderers.putIfAbsent(animatable.getClass(), this);
	}

	public static GeoReplacedEntityRenderer getRenderer(Class<? extends IAnimatable> animatableClass) {
		return renderers.get(animatableClass);
	}

	@AvailableSince(value = "3.1.24")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getWidthScale(Object animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getHeightScale(Object entity) {
		return this.heightScale;
	}

	public static void registerReplacedEntity(Class<? extends IAnimatable> itemClass,
			GeoReplacedEntityRenderer renderer) {
		renderers.put(itemClass, renderer);
	}

	@Override
	public void renderEarly(Object animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource,
			VertexConsumer buffer, int packedLight, int packedOverlayIn, float red, float green, float blue,
			float alpha) {
		this.renderEarlyMat = poseStack.peek().getPositionMatrix().copy();
		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void render(Entity entity, float entityYaw, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, int packedLight) {

		render(entity, this.animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	public void render(Entity entity, IAnimatable animatable, float entityYaw, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, int packedLight) {

		if (!(entity instanceof LivingEntity livingEntity))
			throw new IllegalStateException("Replaced renderer was not an instanceof LivingEntity");

		this.currentAnimatable = animatable;
		this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();
		boolean shouldSit = entity.hasVehicle() && (entity.getVehicle() != null);

		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.push();

		if (entity instanceof MobEntity mob) {
			Entity leashHolder = mob.getHoldingEntity();

			if (leashHolder != null)
				renderLeash(mob, partialTick, poseStack, bufferSource, leashHolder);
		}

		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = livingEntity.isBaby();

		float lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
		float lerpHeadRot = MathHelper.lerpAngleDegrees(partialTick, livingEntity.prevHeadYaw, livingEntity.headYaw);
		float netHeadYaw = lerpHeadRot - lerpBodyRot;

		if (shouldSit && entity.getVehicle()instanceof LivingEntity vehicle) {
			lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, vehicle.prevBodyYaw, vehicle.bodyYaw);
			netHeadYaw = lerpHeadRot - lerpBodyRot;
			float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
			lerpBodyRot = lerpHeadRot - clampedHeadYaw;

			if (clampedHeadYaw * clampedHeadYaw > 2500f)
				lerpBodyRot += clampedHeadYaw * 0.2f;

			netHeadYaw = lerpHeadRot - lerpBodyRot;
		}

		if (entity.getPose() == EntityPose.SLEEPING) {
			Direction direction = livingEntity.getSleepingDirection();

			if (direction != null) {
				float eyeOffset = entity.getEyeHeight(EntityPose.STANDING) - 0.1f;

				poseStack.translate(-direction.getOffsetX() * eyeOffset, 0, -direction.getOffsetZ() * eyeOffset);
			}
		}

		float lerpedAge = livingEntity.age + partialTick;
		float limbSwingAmount = 0;
		float limbSwing = 0;

		applyRotations(livingEntity, poseStack, lerpedAge, lerpBodyRot, partialTick);
		preRenderCallback(livingEntity, poseStack, partialTick);

		if (!shouldSit && entity.isAlive()) {
			limbSwingAmount = Math.min(1,
					MathHelper.lerp(partialTick, livingEntity.lastLimbDistance, livingEntity.limbDistance));
			limbSwing = livingEntity.limbAngle - livingEntity.limbDistance * (1 - partialTick);

			if (livingEntity.isBaby())
				limbSwing *= 3.0F;
		}

		float headPitch = MathHelper.lerp(partialTick, entity.prevPitch, entity.getPitch());
		entityModelData.headPitch = -headPitch;
		entityModelData.netHeadYaw = -netHeadYaw;
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));
		AnimationEvent predicate = new AnimationEvent(animatable, limbSwing, limbSwingAmount, partialTick,
				(limbSwingAmount <= -getSwingMotionAnimThreshold() || limbSwingAmount <= getSwingMotionAnimThreshold()),
				Collections.singletonList(entityModelData));

		this.modelProvider.setLivingAnimations(animatable, getInstanceId(entity), predicate); // TODO change to
																								// setCustomAnimations
																								// in 1.20+
		poseStack.translate(0, 0.01f, 0);
		RenderSystem.setShaderTexture(0, getTexture(entity));

		Color renderColor = getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
		RenderLayer renderType = getRenderType(entity, partialTick, poseStack, bufferSource, null, packedLight,
				getTexture(entity));

		if (!entity.isInvisibleTo(MinecraftClient.getInstance().player)) {
			VertexConsumer glintBuffer = bufferSource.getBuffer(RenderLayer.getDirectEntityGlint());
			VertexConsumer translucentBuffer = bufferSource
					.getBuffer(RenderLayer.getEntityTranslucentCull(getTexture(entity)));
			render(model, entity, partialTick, renderType, poseStack, bufferSource,
					glintBuffer != translucentBuffer ? VertexConsumers.union(glintBuffer, translucentBuffer)
							: null,
					packedLight, getPackedOverlay(livingEntity, getOverlayProgress(livingEntity, partialTick)),
					renderColor.getRed() / 255f, renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
					renderColor.getAlpha() / 255f);
		}

		if (!entity.isSpectator()) {
			for (GeoLayerRenderer layerRenderer : this.layerRenderers) {
				layerRenderer.render(poseStack, bufferSource, packedLight, entity, limbSwing, limbSwingAmount,
						partialTick, lerpedAge, netHeadYaw, headPitch);
			}
		}

		if (FabricLoader.getInstance().isModLoaded("patchouli"))
			PatchouliCompat.patchouliLoaded(poseStack);

		poseStack.pop();
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Entity entity = (Entity) this.animatable;
			Matrix4f poseState = poseStack.peek().getPositionMatrix().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.addToLastColumn(new Vec3f(getPositionOffset(entity, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.addToLastColumn(new Vec3f(entity.getPos()));
			bone.setWorldSpaceXform(worldState);
		}

		IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	protected float getOverlayProgress(LivingEntity entity, float partialTicks) {
		return 0.0F;
	}

	protected void preRenderCallback(LivingEntity entity, MatrixStack poseStack, float partialTick) {
	}

	@Override
	public Identifier getTexture(Entity entity) {
		return getTextureLocation(currentAnimatable);
	}

	@Override
	public AnimatedGeoModel getGeoModelProvider() {
		return this.modelProvider;
	}

	/**
	 * Use {@link IGeoRenderer#getInstanceId(Object)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public Integer getUniqueID(T animatable) {
		return getInstanceId(animatable);
	}

	// TODO 1.20+ change to instance method with T argument instead of entity
	public static int getPackedOverlay(LivingEntity entity, float u) {
		return OverlayTexture.packUv(OverlayTexture.getU(u), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	protected void applyRotations(LivingEntity entity, MatrixStack poseStack, float ageInTicks, float rotationYaw,
			float partialTick) {
		EntityPose pose = entity.getPose();

		if (pose != EntityPose.SLEEPING)
			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - rotationYaw));

		if (entity.deathTime > 0) {
			float deathRotation = (entity.deathTime + partialTick - 1f) / 20f * 1.6f;

			poseStack.multiply(
					Vec3f.POSITIVE_Z.getDegreesQuaternion(Math.min(MathHelper.sqrt(deathRotation), 1) * getDeathMaxRotation(entity)));
		} else if (entity.isUsingRiptide()) {
			poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f - entity.getPitch()));
			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.age + partialTick) * -75f));
		} else if (pose == EntityPose.SLEEPING) {
			Direction bedOrientation = entity.getSleepingDirection();

			poseStack.multiply(
					Vec3f.POSITIVE_Y.getDegreesQuaternion(bedOrientation != null ? getFacingAngle(bedOrientation) : rotationYaw));
			poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(getDeathMaxRotation(entity)));
			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270f));
		} else if (entity.hasCustomName() || entity instanceof PlayerEntity) {
			String name = entity.getName().getString();

			if (entity instanceof PlayerEntity player) {
				if (!player.isPartVisible(PlayerModelPart.CAPE))
					return;
			} else {
				name = Formatting.strip(name);
			}

			if (name != null && (name.equals("Dinnerbone") || name.equalsIgnoreCase("Grumm"))) {
				poseStack.translate(0, entity.getHeight() + 0.1f, 0);
				poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180f));
			}
		}
	}

	protected boolean isVisible(LivingEntity entity) {
		return !entity.isInvisible();
	}

	private static float getFacingAngle(Direction facingIn) {
		return switch (facingIn) {
		case SOUTH -> 90;
		case NORTH -> 270;
		case EAST -> 180;
		default -> 0;
		};
	}

	protected float getDeathMaxRotation(LivingEntity entity) {
		return 90;
	}

	@Override
	public boolean hasLabel(Entity entity) {
		double nameRenderDistance = entity.isSneaky() ? 32d : 64d;

		if (this.dispatcher.getSquaredDistanceToCamera(entity) >= nameRenderDistance * nameRenderDistance)
			return false;

		return entity == this.dispatcher.targetedEntity && entity.hasCustomName()
				&& MinecraftClient.isHudEnabled();
	}

	protected float getSwingProgress(LivingEntity entity, float partialTick) {
		return entity.getHandSwingProgress(partialTick);
	}

	/**
	 * Determines how far (from 0) the arm swing should be moving before counting as
	 * moving for animation purposes.
	 */
	protected float getSwingMotionAnimThreshold() {
		return 0.15f;
	}

	@Override
	public Identifier getTextureLocation(Object animatable) {
		return this.modelProvider.getTextureLocation((IAnimatable) animatable);
	}

	public final boolean addLayer(GeoLayerRenderer<? extends LivingEntity> layer) {
		return this.layerRenderers.add(layer);
	}

	public <E extends Entity> void renderLeash(MobEntity entity, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, E leashHolder) {
		double lerpBodyAngle = (MathHelper.lerp(partialTick, entity.bodyYaw, entity.prevBodyYaw) * MathHelper.RADIANS_PER_DEGREE)
				+ MathHelper.HALF_PI;
		Vec3d leashOffset = entity.getLeashOffset();
		double xAngleOffset = Math.cos(lerpBodyAngle) * leashOffset.z + Math.sin(lerpBodyAngle) * leashOffset.x;
		double zAngleOffset = Math.sin(lerpBodyAngle) * leashOffset.z - Math.cos(lerpBodyAngle) * leashOffset.x;
		double lerpOriginX = MathHelper.lerp(partialTick, entity.prevX, entity.getX()) + xAngleOffset;
		double lerpOriginY = MathHelper.lerp(partialTick, entity.prevY, entity.getY()) + leashOffset.y;
		double lerpOriginZ = MathHelper.lerp(partialTick, entity.prevZ, entity.getZ()) + zAngleOffset;
		Vec3d ropeGripPosition = leashHolder.getLeashPos(partialTick);
		float xDif = (float) (ropeGripPosition.x - lerpOriginX);
		float yDif = (float) (ropeGripPosition.y - lerpOriginY);
		float zDif = (float) (ropeGripPosition.z - lerpOriginZ);
		float offsetMod = MathHelper.fastInverseSqrt(xDif * xDif + zDif * zDif) * 0.025f / 2f;
		float xOffset = zDif * offsetMod;
		float zOffset = xDif * offsetMod;
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getLeash());
		BlockPos entityEyePos = new BlockPos(entity.getCameraPosVec(partialTick));
		BlockPos holderEyePos = new BlockPos(leashHolder.getCameraPosVec(partialTick));
		int entityBlockLight = getBlockLight(entity, entityEyePos);
		int holderBlockLight = leashHolder.isOnFire() ? 15
				: leashHolder.world.getLightLevel(LightType.BLOCK, holderEyePos);
		int entitySkyLight = entity.world.getLightLevel(LightType.SKY, entityEyePos);
		int holderSkyLight = entity.world.getLightLevel(LightType.SKY, holderEyePos);

		poseStack.push();
		poseStack.translate(xAngleOffset, leashOffset.y, zAngleOffset);

		Matrix4f posMatrix = poseStack.peek().getPositionMatrix();

		for (int segment = 0; segment <= 24; ++segment) {
			renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.025f, xOffset, zOffset, segment, false);
		}

		for (int segment = 24; segment >= 0; --segment) {
			renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.0f, xOffset, zOffset, segment, true);
		}

		poseStack.pop();
	}

	private static void renderLeashPiece(VertexConsumer buffer, Matrix4f positionMatrix, float xDif, float yDif,
			float zDif, int entityBlockLight, int holderBlockLight, int entitySkyLight, int holderSkyLight, float width,
			float yOffset, float xOffset, float zOffset, int segment, boolean isLeashKnot) {
		float piecePosPercent = segment / 24f;
		int lerpBlockLight = (int) MathHelper.lerp(piecePosPercent, entityBlockLight, holderBlockLight);
		int lerpSkyLight = (int) MathHelper.lerp(piecePosPercent, entitySkyLight, holderSkyLight);
		int packedLight = LightmapTextureManager.pack(lerpBlockLight, lerpSkyLight);
		float knotColourMod = segment % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1f;
		float red = 0.5f * knotColourMod;
		float green = 0.4f * knotColourMod;
		float blue = 0.3f * knotColourMod;
		float x = xDif * piecePosPercent;
		float y = yDif > 0.0f ? yDif * piecePosPercent * piecePosPercent
				: yDif - yDif * (1.0f - piecePosPercent) * (1.0f - piecePosPercent);
		float z = zDif * piecePosPercent;

		buffer.vertex(positionMatrix, x - xOffset, y + yOffset, z + zOffset).color(red, green, blue, 1).light(packedLight)
				.next();
		buffer.vertex(positionMatrix, x + xOffset, y + width - yOffset, z - zOffset).color(red, green, blue, 1)
				.light(packedLight).next();
	}

	@Override
	public void setCurrentRTB(VertexConsumerProvider bufferSource) {
		this.rtb = bufferSource;
	}

	@Override
	public VertexConsumerProvider getCurrentRTB() {
		return this.rtb;
	}

	/**
	 * Just add them yourself.<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected float handleRotationFloat(LivingEntity livingBase, float partialTicks) {
		return (float) livingBase.age + partialTicks;
	}
}
