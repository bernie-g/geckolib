package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.List;

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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LightType;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.util.AnimationUtils;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public abstract class GeoEntityRenderer<T extends LivingEntity & IAnimatable> extends EntityRenderer<T>
		implements IGeoRenderer<T> {
	static {
		AnimationController.addModelFetcher(animatable -> animatable instanceof Entity entity ? (IAnimatableModel<Object>)AnimationUtils.getGeoModelForEntity(entity) : null);
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected final List<GeoLayerRenderer<T>> layerRenderers = new ObjectArrayList<>();
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

	/**
	 * Use {@link LivingEntity#getEquippedStack(EquipmentSlot)}
	 */
	@Deprecated
	public ItemStack mainHand, offHand, helmet, chestplate, leggings, boots;
	public VertexConsumerProvider rtb;
	public Identifier whTexture;
	protected float widthScale = 1;
	protected float heightScale = 1;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoEntityRenderer(EntityRendererFactory.Context renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);

		this.modelProvider = modelProvider;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@Override
	public void renderEarly(T animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
							float partialTicks) {
		this.animatable = animatable;
		this.renderEarlyMat = poseStack.peek().getPositionMatrix().copy();
		this.rtb = bufferSource;
		this.whTexture = getTextureLocation(animatable);

		// TODO 1.20+ Remove these in breaking change. Users can retrieve these themselves if needed, this is unnecessary work
		this.mainHand = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
		this.offHand = animatable.getEquippedStack(EquipmentSlot.OFFHAND);
		this.helmet = animatable.getEquippedStack(EquipmentSlot.HEAD);
		this.chestplate = animatable.getEquippedStack(EquipmentSlot.CHEST);
		this.leggings = animatable.getEquippedStack(EquipmentSlot.LEGS);
		this.boots = animatable.getEquippedStack(EquipmentSlot.FEET);

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	public void render(T animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource,
			int packedLight) {
		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.push();

		if (animatable instanceof MobEntity mob) {
			Entity leashHolder = mob.getHoldingEntity();

			if (leashHolder != null)
				renderLeash(animatable, partialTick, poseStack, bufferSource, leashHolder);
		}

		this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();
		boolean shouldSit = animatable.hasVehicle() && (animatable.getVehicle() != null);
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = animatable.isBaby();

		float lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevBodyYaw, animatable.bodyYaw);
		float lerpHeadRot = MathHelper.lerpAngleDegrees(partialTick, animatable.prevHeadYaw, animatable.headYaw);
		float netHeadYaw = lerpHeadRot - lerpBodyRot;

		if (shouldSit && animatable.getVehicle() instanceof LivingEntity livingentity) {
			lerpBodyRot = MathHelper.lerpAngleDegrees(partialTick, livingentity.prevBodyYaw, livingentity.bodyYaw);
			netHeadYaw = lerpHeadRot - lerpBodyRot;
			float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
			lerpBodyRot = lerpHeadRot - clampedHeadYaw;

			if (clampedHeadYaw * clampedHeadYaw > 2500f)
				lerpBodyRot += clampedHeadYaw * 0.2f;

			netHeadYaw = lerpHeadRot - lerpBodyRot;
		}

		if (animatable.getPose() == EntityPose.SLEEPING) {
			Direction bedDirection = animatable.getSleepingDirection();

			if (bedDirection != null) {
				float eyePosOffset = animatable.getEyeHeight(EntityPose.STANDING) - 0.1F;

				poseStack.translate(-bedDirection.getOffsetX() * eyePosOffset, 0, -bedDirection.getOffsetZ() * eyePosOffset);
			}
		}

		float ageInTicks = animatable.age + partialTick;
		float limbSwingAmount = 0;
		float limbSwing = 0;

		applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

		if (!shouldSit && animatable.isAlive()) {
			limbSwingAmount = MathHelper.lerp(partialTick, animatable.lastLimbDistance, animatable.limbDistance);
			limbSwing = animatable.limbAngle - animatable.limbDistance * (1 - partialTick);

			if (animatable.isBaby())
				limbSwing *= 3f;

			if (limbSwingAmount > 1f)
				limbSwingAmount = 1f;
		}

		float headPitch = MathHelper.lerp(partialTick, animatable.prevPitch, animatable.getPitch());
		entityModelData.headPitch = -headPitch;
		entityModelData.netHeadYaw = -netHeadYaw;

		AnimationEvent<T> predicate = new AnimationEvent<T>(animatable, limbSwing, limbSwingAmount, partialTick,
				(limbSwingAmount <= -getSwingMotionAnimThreshold() || limbSwingAmount > getSwingMotionAnimThreshold()), Collections.singletonList(entityModelData));
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelLocation(animatable));

		this.modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), predicate); // TODO change to setCustomAnimations in 1.20+

		poseStack.translate(0, 0.01f, 0);
		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

		Color renderColor = getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
		RenderLayer renderType = getRenderType(animatable, partialTick, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));

		if (!animatable.isInvisibleTo(MinecraftClient.getInstance().player)) {
			VertexConsumer glintBuffer = bufferSource.getBuffer(RenderLayer.getDirectEntityGlint());
			VertexConsumer translucentBuffer = bufferSource
					.getBuffer(RenderLayer.getEntityTranslucentCull(getTextureLocation(animatable)));

			render(model, animatable, partialTick, renderType, poseStack, bufferSource,
					glintBuffer != translucentBuffer ? VertexConsumers.union(glintBuffer, translucentBuffer)
							: null,
					packedLight, getOverlay(animatable, 0), renderColor.getRed() / 255f,
					renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
					renderColor.getAlpha() / 255f);
		}

		if (!animatable.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				renderLayer(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, ageInTicks,
						netHeadYaw, headPitch, bufferSource, layerRenderer);
			}
		}

		if (FabricLoader.getInstance().isModLoaded("patchouli"))
			PatchouliCompat.patchouliLoaded(poseStack);

		poseStack.pop();

		super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.push();
		RenderUtils.translateMatrixToBone(poseStack, bone);
		RenderUtils.translateToPivotPoint(poseStack, bone);

		boolean rotOverride = bone.rotMat != null;

		if (rotOverride) {
			poseStack.peek().getPositionMatrix().multiply(bone.rotMat);
			poseStack.peek().getNormalMatrix().multiply(new Matrix3f(bone.rotMat));
		}
		else {
			RenderUtils.rotateMatrixAroundBone(poseStack, bone);
		}

		RenderUtils.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.peek().getPositionMatrix().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.addToLastColumn(new Vec3f(getPositionOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.addToLastColumn(new Vec3f(this.animatable.getPos()));
			bone.setWorldSpaceXform(worldState);
		}

		RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

		if (!bone.isHidden) {
			if (!bone.cubesAreHidden()) {
				for (GeoCube geoCube : bone.childCubes) {
					poseStack.push();
					renderCube(geoCube, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
					poseStack.pop();
				}
			}

			for (GeoBone childBone : bone.childBones) {
				renderRecursively(childBone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			}
		}

		poseStack.pop();
	}

	protected void renderLayer(MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, T animatable,
							   float limbSwing, float limbSwingAmount, float partialTick, float rotFloat, float netHeadYaw,
							   float headPitch, VertexConsumerProvider bufferSource2, GeoLayerRenderer<T> layerRenderer) {
		layerRenderer.render(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, rotFloat,
				netHeadYaw, headPitch);
	}

	/**
	 * Use {@link IGeoRenderer#getInstanceId(Object)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public Integer getUniqueID(T animatable) {
		return getInstanceId(animatable);
	}

	@Override
	public int getInstanceId(T animatable) {
		return animatable.getId();
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@AvailableSince(value = "3.0.75")
	public int getOverlay(T entity, float u) {
		return OverlayTexture.packUv(OverlayTexture.getU(u),
				OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	/**
	 * Use {@link GeoEntityRenderer#getOverlay(T, float)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public int getPackedOverlay(LivingEntity entity, float u) {
		return this.getOverlay(animatable, u);
	}

	protected void applyRotations(T animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw,
			float partialTick) {
		EntityPose pose = animatable.getPose();

		if (pose != EntityPose.SLEEPING)
			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - rotationYaw));

		if (animatable.deathTime > 0) {
			float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;

			poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(Math.min(MathHelper.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
		}
		else if (animatable.isUsingRiptide()) {
			poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f - animatable.getPitch()));
			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((animatable.age + partialTick) * -75f));
		}
		else if (pose == EntityPose.SLEEPING) {
			Direction bedOrientation = animatable.getSleepingDirection();

			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(bedOrientation != null ? getFacingAngle(bedOrientation) : rotationYaw));
			poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(getDeathMaxRotation(animatable)));
			poseStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270f));
		}
		else if (animatable.hasCustomName() || animatable instanceof PlayerEntity) {
			String name = animatable.getName().getString();

			if (animatable instanceof PlayerEntity player) {
				if (!player.isPartVisible(PlayerModelPart.CAPE))
					return;
			}
			else {
				name = Formatting.strip(name);
			}

			if (name != null && (name.equals("Dinnerbone") || name.equalsIgnoreCase("Grumm"))) {
				poseStack.translate(0, animatable.getHeight() + 0.1f, 0);
				poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180f));
			}
		}
	}

	protected boolean isVisible(T animatable) {
		return !animatable.isInvisible();
	}

	private static float getFacingAngle(Direction direction) {
		return switch (direction) {
			case SOUTH -> 90f;
			case NORTH -> 270f;
			case EAST -> 180f;
			default -> 0f;
		};
	}

	protected float getDeathMaxRotation(T animatable) {
		return 90f;
	}

	@Override
	public boolean hasLabel(T animatable) {
		double nameRenderDistance = animatable.isSneaking() ? 32d : 64d;

		if (this.dispatcher.getSquaredDistanceToCamera(animatable) >= nameRenderDistance * nameRenderDistance)
			return false;

		return animatable == this.dispatcher.targetedEntity && animatable.hasCustomName() && MinecraftClient.isHudEnabled();
	}

	protected float getSwingProgress(T animatable, float partialTick) {
		return animatable.getHandSwingProgress(partialTick);
	}

	/**
	 * Determines how far (from 0) the arm swing should be moving before counting as moving for animation purposes.
	 */
	protected float getSwingMotionAnimThreshold() {
		return 0.15f;
	}

	@Override
	public Identifier getTextureLocation(T animatable) {
		return this.modelProvider.getTextureLocation(animatable);
	}
	
	@Override
	public Identifier getTexture(T entity) {
		return this.modelProvider.getTextureLocation(entity);
	}

	public final boolean addLayer(GeoLayerRenderer<T> layer) {
		return this.layerRenderers.add(layer);
	}

	public <E extends Entity> void renderLeash(T entity, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, E leashHolder) {
		double lerpBodyAngle = (MathHelper.lerp(partialTick, entity.bodyYaw, entity.prevBodyYaw) * MathHelper.RADIANS_PER_DEGREE) + MathHelper.HALF_PI;
		Vec3d leashOffset = entity.getLeashOffset();
		double xAngleOffset = Math.cos(lerpBodyAngle) * leashOffset.z + Math.sin(lerpBodyAngle) * leashOffset.x;
		double zAngleOffset = Math.sin(lerpBodyAngle) * leashOffset.z - Math.cos(lerpBodyAngle) * leashOffset.x;
		double lerpOriginX = MathHelper.lerp(partialTick, entity.prevX, entity.getX()) + xAngleOffset;
		double lerpOriginY = MathHelper.lerp(partialTick, entity.prevY, entity.getY()) + leashOffset.y;
		double lerpOriginZ = MathHelper.lerp(partialTick, entity.prevZ, entity.getZ()) + zAngleOffset;
		Vec3d ropeGripPosition = leashHolder.getLeashPos(partialTick);
		float xDif = (float)(ropeGripPosition.x - lerpOriginX);
		float yDif = (float)(ropeGripPosition.y - lerpOriginY);
		float zDif = (float)(ropeGripPosition.z - lerpOriginZ);
		float offsetMod = MathHelper.fastInverseSqrt(xDif * xDif + zDif * zDif) * 0.025f / 2f;
		float xOffset = zDif * offsetMod;
		float zOffset = xDif * offsetMod;
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getLeash());
		BlockPos entityEyePos = new BlockPos(entity.getCameraPosVec(partialTick));
		BlockPos holderEyePos = new BlockPos(leashHolder.getCameraPosVec(partialTick));
		int entityBlockLight = getBlockLight(entity, entityEyePos);
		int holderBlockLight = leashHolder.isOnFire() ? 15 : leashHolder.world.getLightLevel(LightType.BLOCK, holderEyePos);
		int entitySkyLight = entity.world.getLightLevel(LightType.SKY, entityEyePos);
		int holderSkyLight = entity.world.getLightLevel(LightType.SKY, holderEyePos);

		poseStack.push();
		poseStack.translate(xAngleOffset, leashOffset.y, zAngleOffset);

		Matrix4f posMatrix = poseStack.peek().getPositionMatrix();

		for (int segment = 0; segment <= 24; ++segment) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.025f, xOffset, zOffset, segment, false);
		}

		for (int segment = 24; segment >= 0; --segment) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.0f, xOffset, zOffset, segment, true);
		}

		poseStack.pop();
	}

	private static void renderLeashPiece(VertexConsumer buffer, Matrix4f positionMatrix, float xDif, float yDif,
										 float zDif, int entityBlockLight, int holderBlockLight, int entitySkyLight,
										 int holderSkyLight, float width, float yOffset, float xOffset, float zOffset, int segment, boolean isLeashKnot) {
		float piecePosPercent = segment / 24f;
		int lerpBlockLight = (int)MathHelper.lerp(piecePosPercent, entityBlockLight, holderBlockLight);
		int lerpSkyLight = (int)MathHelper.lerp(piecePosPercent, entitySkyLight, holderSkyLight);
		int packedLight = LightmapTextureManager.pack(lerpBlockLight, lerpSkyLight);
		float knotColourMod = segment % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1f;
		float red = 0.5f * knotColourMod;
		float green = 0.4f * knotColourMod;
		float blue = 0.3f * knotColourMod;
		float x = xDif * piecePosPercent;
		float y = yDif > 0.0f ? yDif * piecePosPercent * piecePosPercent : yDif - yDif * (1.0f - piecePosPercent) * (1.0f - piecePosPercent);
		float z = zDif * piecePosPercent;

		buffer.vertex(positionMatrix, x - xOffset, y + yOffset, z + zOffset).color(red, green, blue, 1).light(packedLight).next();
		buffer.vertex(positionMatrix, x + xOffset, y + width - yOffset, z - zOffset).color(red, green, blue, 1).light(packedLight).next();
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
	 * Just add them yourself<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected float getLerpedAge(T animatable, float partialTick) {
		return animatable.age + partialTick;
	}
}
