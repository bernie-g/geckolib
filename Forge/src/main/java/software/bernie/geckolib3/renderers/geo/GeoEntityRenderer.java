package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.ApiStatus.AvailableSince;
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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

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
	 * Use {@link LivingEntity#getItemBySlot(EquipmentSlot)}
	 */
	@Deprecated
	public ItemStack mainHand, offHand, helmet, chestplate, leggings, boots;
	public MultiBufferSource rtb;
	public ResourceLocation whTexture;
	protected float widthScale = 1;
	protected float heightScale = 1;
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	public GeoEntityRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);

		this.modelProvider = modelProvider;
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

	@Override
	public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
							VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
							float partialTicks) {
		this.animatable = animatable;
		this.renderEarlyMat = poseStack.last().pose().copy();
		this.rtb = bufferSource;
		this.whTexture = getTextureLocation(animatable);

		// TODO 1.20+ Remove these in breaking change. Users can retrieve these themselves if needed, this is unnecessary work
		this.mainHand = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
		this.offHand = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
		this.helmet = animatable.getItemBySlot(EquipmentSlot.HEAD);
		this.chestplate = animatable.getItemBySlot(EquipmentSlot.CHEST);
		this.leggings = animatable.getItemBySlot(EquipmentSlot.LEGS);
		this.boots = animatable.getItemBySlot(EquipmentSlot.FEET);

		IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight,
				packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	public void render(T animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
			int packedLight) {
		setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		poseStack.pushPose();

		if (animatable instanceof Mob mob) {
			Entity leashHolder = mob.getLeashHolder();

			if (leashHolder != null)
				renderLeash(animatable, partialTick, poseStack, bufferSource, leashHolder);
		}

		this.dispatchedMat = poseStack.last().pose().copy();
		boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null && animatable.getVehicle().shouldRiderSit());
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = animatable.isBaby();

		float lerpBodyRot = Mth.rotLerp(partialTick, animatable.yBodyRotO, animatable.yBodyRot);
		float lerpHeadRot = Mth.rotLerp(partialTick, animatable.yHeadRotO, animatable.yHeadRot);
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

		if (animatable.getPose() == Pose.SLEEPING) {
			Direction bedDirection = animatable.getBedOrientation();

			if (bedDirection != null) {
				float eyePosOffset = animatable.getEyeHeight(Pose.STANDING) - 0.1F;

				poseStack.translate(-bedDirection.getStepX() * eyePosOffset, 0, -bedDirection.getStepZ() * eyePosOffset);
			}
		}

		float ageInTicks = animatable.tickCount + partialTick;
		float limbSwingAmount = 0;
		float limbSwing = 0;

		applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick);

		if (!shouldSit && animatable.isAlive()) {
			limbSwingAmount = Mth.lerp(partialTick, animatable.animationSpeedOld, animatable.animationSpeed);
			limbSwing = animatable.animationPosition - animatable.animationSpeed * (1 - partialTick);

			if (animatable.isBaby())
				limbSwing *= 3f;

			if (limbSwingAmount > 1f)
				limbSwingAmount = 1f;
		}

		float headPitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
		entityModelData.headPitch = -headPitch;
		entityModelData.netHeadYaw = -netHeadYaw;

		AnimationEvent<T> predicate = new AnimationEvent<T>(animatable, limbSwing, limbSwingAmount, partialTick,
				(limbSwingAmount <= -getSwingMotionAnimThreshold() || limbSwingAmount <= getSwingMotionAnimThreshold()), Collections.singletonList(entityModelData));
		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(animatable));

		this.modelProvider.setLivingAnimations(animatable, getInstanceId(animatable), predicate); // TODO change to setCustomAnimations in 1.20+

		poseStack.translate(0, 0.01f, 0);
		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));

		Color renderColor = getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
		RenderType renderType = getRenderType(animatable, partialTick, poseStack, bufferSource, null, packedLight,
				getTextureLocation(animatable));

		if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) {
			VertexConsumer glintBuffer = bufferSource.getBuffer(RenderType.entityGlintDirect());
			VertexConsumer translucentBuffer = bufferSource
					.getBuffer(RenderType.entityTranslucentCull(getTextureLocation(animatable)));

			render(model, animatable, partialTick, renderType, poseStack, bufferSource,
					glintBuffer != translucentBuffer ? VertexMultiConsumer.create(glintBuffer, translucentBuffer)
							: null,
					packedLight, getPackedOverlay(animatable, 0), renderColor.getRed() / 255f,
					renderColor.getGreen() / 255f, renderColor.getBlue() / 255f,
					renderColor.getAlpha() / 255f);
		}

		if (!animatable.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				renderLayer(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, ageInTicks,
						netHeadYaw, headPitch, bufferSource, layerRenderer);
			}
		}

		if (ModList.get().isLoaded("patchouli"))
			PatchouliCompat.patchouliLoaded(poseStack);

		poseStack.popPose();

		super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();
		RenderUtils.translateMatrixToBone(poseStack, bone);
		RenderUtils.translateToPivotPoint(poseStack, bone);

		boolean rotOverride = bone.rotMat != null;

		if (rotOverride) {
			poseStack.last().pose().multiply(bone.rotMat);
			poseStack.last().normal().mul(new Matrix3f(bone.rotMat));
		}
		else {
			RenderUtils.rotateMatrixAroundBone(poseStack, bone);
		}

		RenderUtils.scaleMatrixForBone(poseStack, bone);

		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

			bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
			localMatrix.translate(new Vector3f(getRenderOffset(this.animatable, 1)));
			bone.setLocalSpaceXform(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(this.animatable.position()));
			bone.setWorldSpaceXform(worldState);
		}

		RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

		if (!bone.isHidden) {
			if (!bone.cubesAreHidden()) {
				for (GeoCube geoCube : bone.childCubes) {
					poseStack.pushPose();
					renderCube(geoCube, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
					poseStack.popPose();
				}
			}

			for (GeoBone childBone : bone.childBones) {
				renderRecursively(childBone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
			}
		}

		poseStack.popPose();
	}

	protected void renderLayer(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T animatable,
							   float limbSwing, float limbSwingAmount, float partialTick, float rotFloat, float netHeadYaw,
							   float headPitch, MultiBufferSource bufferSource2, GeoLayerRenderer<T> layerRenderer) {
		layerRenderer.render(poseStack, bufferSource, packedLight, animatable, limbSwing, limbSwingAmount, partialTick, rotFloat,
				netHeadYaw, headPitch);
	}

	@Override
	public int getInstanceId(T animatable) {
		return animatable.getId();
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getWidthScale(T animatable) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.24")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	// TODO 1.20+ change to instance method with T argument instead of entity
	public static int getPackedOverlay(LivingEntity entity, float u) {
		return OverlayTexture.pack(OverlayTexture.u(u),
				OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw,
			float partialTick) {
		Pose pose = animatable.getPose();

		if (pose != Pose.SLEEPING)
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180f - rotationYaw));

		if (animatable.deathTime > 0) {
			float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;

			poseStack.mulPose(Vector3f.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
		}
		else if (animatable.isAutoSpinAttack()) {
			poseStack.mulPose(Vector3f.XP.rotationDegrees(-90f - animatable.getXRot()));
			poseStack.mulPose(Vector3f.YP.rotationDegrees((animatable.tickCount + partialTick) * -75f));
		}
		else if (pose == Pose.SLEEPING) {
			Direction bedOrientation = animatable.getBedOrientation();

			poseStack.mulPose(Vector3f.YP.rotationDegrees(bedOrientation != null ? getFacingAngle(bedOrientation) : rotationYaw));
			poseStack.mulPose(Vector3f.ZP.rotationDegrees(getDeathMaxRotation(animatable)));
			poseStack.mulPose(Vector3f.YP.rotationDegrees(270f));
		}
		else if (animatable.hasCustomName() || animatable instanceof Player) {
			String name = animatable.getName().getString();

			if (animatable instanceof Player player) {
				if (!player.isModelPartShown(PlayerModelPart.CAPE))
					return;
			}
			else {
				name = ChatFormatting.stripFormatting(name);
			}

			if (name != null && (name.equals("Dinnerbone") || name.equalsIgnoreCase("Grumm"))) {
				poseStack.translate(0, animatable.getBbHeight() + 0.1f, 0);
				poseStack.mulPose(Vector3f.ZP.rotationDegrees(180f));
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
	public boolean shouldShowName(T animatable) {
		double nameRenderDistance = animatable.isDiscrete() ? 32d : 64d;

		if (this.entityRenderDispatcher.distanceToSqr(animatable) >= nameRenderDistance * nameRenderDistance)
			return false;

		return animatable == this.entityRenderDispatcher.crosshairPickEntity && animatable.hasCustomName() && Minecraft.renderNames();
	}

	protected float getSwingProgress(T animatable, float partialTick) {
		return animatable.getAttackAnim(partialTick);
	}

	/**
	 * Determines how far (from 0) the arm swing should be moving before counting as moving for animation purposes.
	 */
	protected float getSwingMotionAnimThreshold() {
		return 0.15f;
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return this.modelProvider.getTextureResource(animatable);
	}

	public final boolean addLayer(GeoLayerRenderer<T> layer) {
		return this.layerRenderers.add(layer);
	}

	public <E extends Entity> void renderLeash(T entity, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, E leashHolder) {
		double lerpBodyAngle = (Mth.lerp(partialTick, entity.yBodyRot, entity.yBodyRotO) * Mth.DEG_TO_RAD) + Mth.HALF_PI;
		Vec3 leashOffset = entity.getLeashOffset();
		double xAngleOffset = Math.cos(lerpBodyAngle) * leashOffset.z + Math.sin(lerpBodyAngle) * leashOffset.x;
		double zAngleOffset = Math.sin(lerpBodyAngle) * leashOffset.z - Math.cos(lerpBodyAngle) * leashOffset.x;
		double lerpOriginX = Mth.lerp(partialTick, entity.xo, entity.getX()) + xAngleOffset;
		double lerpOriginY = Mth.lerp(partialTick, entity.yo, entity.getY()) + leashOffset.y;
		double lerpOriginZ = Mth.lerp(partialTick, entity.zo, entity.getZ()) + zAngleOffset;
		Vec3 ropeGripPosition = leashHolder.getRopeHoldPosition(partialTick);
		float xDif = (float)(ropeGripPosition.x - lerpOriginX);
		float yDif = (float)(ropeGripPosition.y - lerpOriginY);
		float zDif = (float)(ropeGripPosition.z - lerpOriginZ);
		float offsetMod = Mth.fastInvSqrt(xDif * xDif + zDif * zDif) * 0.025f / 2f;
		float xOffset = zDif * offsetMod;
		float zOffset = xDif * offsetMod;
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
		BlockPos entityEyePos = new BlockPos(entity.getEyePosition(partialTick));
		BlockPos holderEyePos = new BlockPos(leashHolder.getEyePosition(partialTick));
		int entityBlockLight = getBlockLightLevel(entity, entityEyePos);
		int holderBlockLight = leashHolder.isOnFire() ? 15 : leashHolder.level.getBrightness(LightLayer.BLOCK, holderEyePos);
		int entitySkyLight = entity.level.getBrightness(LightLayer.SKY, entityEyePos);
		int holderSkyLight = entity.level.getBrightness(LightLayer.SKY, holderEyePos);

		poseStack.pushPose();
		poseStack.translate(xAngleOffset, leashOffset.y, zAngleOffset);

		Matrix4f posMatrix = poseStack.last().pose();

		for (int segment = 0; segment <= 24; ++segment) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.025f, xOffset, zOffset, segment, false);
		}

		for (int segment = 24; segment >= 0; --segment) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, posMatrix, xDif, yDif, zDif, entityBlockLight, holderBlockLight,
					entitySkyLight, holderSkyLight, 0.025f, 0.0f, xOffset, zOffset, segment, true);
		}

		poseStack.popPose();
	}

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

	@Override
	public void setCurrentRTB(MultiBufferSource bufferSource) {
		this.rtb = bufferSource;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}

	/**
	 * Just add them yourself<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected float getLerpedAge(T animatable, float partialTick) {
		return animatable.tickCount + partialTick;
	}
}
