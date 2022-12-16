package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.LightType;
import net.minecraftforge.fml.ModList;
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
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Entity) {
				return (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity((Entity) object);
			}
			return null;
		});
	}

	protected final AnimatedGeoModel<T> modelProvider;
	protected final List<GeoLayerRenderer<T>> layerRenderers = new ObjectArrayList<>();
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

	public ItemStack mainHand;
	public ItemStack offHand;
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public IRenderTypeBuffer rtb;
	public ResourceLocation whTexture;
	protected float widthScale = 1;
	protected float heightScale = 1;

	public GeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.0.95")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.95")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn,
			int packedLightIn) {
		this.dispatchedMat = stack.last().pose().copy();
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		stack.pushPose();
		if (entity instanceof MobEntity) {
			Entity leashHolder = ((MobEntity) entity).getLeashHolder();
			if (leashHolder != null) {
				this.renderLeash(entity, partialTicks, stack, bufferIn, leashHolder);
			}
		}
		boolean shouldSit = entity.isPassenger()
				&& (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = entity.isBaby();

		float f = MathHelper.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		float f1 = MathHelper.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
		float netHeadYaw = f1 - f;
		if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entity.getVehicle();
			f = MathHelper.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
			netHeadYaw = f1 - f;
			float f3 = MathHelper.wrapDegrees(netHeadYaw);
			if (f3 < -85.0F) {
				f3 = -85.0F;
			}

			if (f3 >= 85.0F) {
				f3 = 85.0F;
			}

			f = f1 - f3;
			if (f3 * f3 > 2500.0F) {
				f += f3 * 0.2F;
			}

			netHeadYaw = f1 - f;
		}

		float headPitch = MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot);
		if (entity.getPose() == Pose.SLEEPING) {
			Direction direction = entity.getBedOrientation();
			if (direction != null) {
				float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				stack.translate((double) ((float) (-direction.getStepX()) * f4), 0.0D,
						(double) ((float) (-direction.getStepZ()) * f4));
			}
		}
		float f7 = this.handleRotationFloat(entity, partialTicks);
		this.applyRotations(entity, stack, f7, f, partialTicks);

		float limbSwingAmount = 0.0F;
		float limbSwing = 0.0F;
		if (!shouldSit && entity.isAlive()) {
			limbSwingAmount = MathHelper.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
			limbSwing = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);
			if (entity.isBaby()) {
				limbSwing *= 3.0F;
			}

			if (limbSwingAmount > 1.0F) {
				limbSwingAmount = 1.0F;
			}
		}
		entityModelData.headPitch = -headPitch;
		entityModelData.netHeadYaw = -netHeadYaw;

		AnimationEvent<T> predicate = new AnimationEvent<T>(entity, limbSwing, limbSwingAmount, partialTicks,
				!(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F), Collections.singletonList(entityModelData));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entity));
		if (modelProvider instanceof IAnimatableModel) {
			((IAnimatableModel<T>) modelProvider).setCustomAnimations(entity, this.getUniqueID(entity), predicate);
		}

		stack.translate(0, 0.01f, 0);
		Minecraft.getInstance().textureManager.bind(getTextureLocation(entity));
		Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn,
				getTextureLocation(entity));
		if (!entity.isInvisibleTo(Minecraft.getInstance().player)) {
			IVertexBuilder glintBuffer = bufferIn.getBuffer(RenderType.entityGlintDirect());
			IVertexBuilder translucentBuffer = bufferIn
					.getBuffer(RenderType.entityTranslucentCull(getTextureLocation(entity)));
			render(model, entity, partialTicks, renderType, stack, bufferIn,
					glintBuffer != translucentBuffer ? VertexBuilderUtils.create(glintBuffer, translucentBuffer) : null,
					packedLightIn, getOverlay(entity, 0), (float) renderColor.getRed() / 255f,
					(float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
					(float) renderColor.getAlpha() / 255);
		}

		if (!entity.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				this.renderLayer(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, f7,
						netHeadYaw, headPitch, bufferIn, layerRenderer);
			}
		}
		if (ModList.get().isLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	protected void renderLayer(MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
			float limbSwing, float limbSwingAmount, float partialTicks, float rotFloat, float netHeadYaw,
			float headPitch, IRenderTypeBuffer bufferIn2, GeoLayerRenderer<T> layerRenderer) {
		layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, rotFloat,
				netHeadYaw, headPitch);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUUID().hashCode();
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer,
			IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		renderEarlyMat = stackIn.last().pose().copy();
		this.animatable = animatable;
		this.mainHand = animatable.getItemBySlot(EquipmentSlotType.MAINHAND);
		this.offHand = animatable.getItemBySlot(EquipmentSlotType.OFFHAND);
		this.helmet = animatable.getItemBySlot(EquipmentSlotType.HEAD);
		this.chestplate = animatable.getItemBySlot(EquipmentSlotType.CHEST);
		this.leggings = animatable.getItemBySlot(EquipmentSlotType.LEGS);
		this.boots = animatable.getItemBySlot(EquipmentSlotType.FEET);
		this.rtb = renderTypeBuffer;
		this.whTexture = this.getTextureLocation(animatable);
		IGeoRenderer.super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, partialTicks);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		stack.pushPose();
		boolean rotOverride = bone.rotMat != null;
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		if (rotOverride) {
			stack.last().pose().multiply(bone.rotMat);
			stack.last().normal().mul(new Matrix3f(bone.rotMat));
		} else {
			RenderUtils.rotate(bone, stack);
		}
		RenderUtils.scale(bone, stack);
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.last();
			Matrix4f boneMat = entry.pose().copy();

			// Model space
			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			Matrix4f modelPosBoneMat = boneMat.copy();
			modelPosBoneMat.multiplyBackward(renderEarlyMatInvert);
			bone.setModelSpaceXform(modelPosBoneMat);

			// Local space
			Matrix4f dispatchedMatInvert = this.dispatchedMat.copy();
			dispatchedMatInvert.invert();
			Matrix4f localPosBoneMat = boneMat.copy();
			localPosBoneMat.multiplyBackward(dispatchedMatInvert);
			// (Offset is the only transform we may want to preserve from the dispatched mat)
			Vector3d renderOffset = this.getRenderOffset(animatable, 1.0F);
			localPosBoneMat.translate(new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			worldPosBoneMat.translate(new Vector3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		RenderUtils.moveBackFromPivot(bone, stack);
		if (!bone.isHidden) {
			Iterator<?> var10 = bone.childCubes.iterator();

			while (var10.hasNext()) {
				GeoCube cube = (GeoCube) var10.next();
				stack.pushPose();
				if (!bone.cubesAreHidden()) {
					this.renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				}
				stack.popPose();
			}
		}
		if (!bone.childBonesAreHiddenToo()) {
			Iterator<?> var10 = bone.childBones.iterator();

			while (var10.hasNext()) {
				GeoBone childBone = (GeoBone) var10.next();
				this.renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
						alpha);
			}
		}

		stack.popPose();
	}

	@Override
	public void preparePositionRotationScale(GeoBone bone, MatrixStack stack) {
		boolean rotOverride = bone.rotMat != null;
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		if (rotOverride) {
			stack.last().pose().multiply(bone.rotMat);
			stack.last().normal().mul(new Matrix3f(bone.rotMat));
		} else {
			RenderUtils.rotate(bone, stack);
		}
		RenderUtils.scale(bone, stack);
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.last();
			Matrix4f matBone = entry.pose().copy();
			bone.setWorldSpaceXform(matBone.copy());

			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			matBone.multiplyBackward(renderEarlyMatInvert);
			bone.setModelSpaceXform(matBone);
		}
		RenderUtils.moveBackFromPivot(bone, stack);
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.0.95")
	@Override
	public float getWidthScale(T animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.95")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@AvailableSince(value = "3.0.103")
	public int getOverlay(T entity, float u) {
		return OverlayTexture.pack(OverlayTexture.u(u),
				OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	/**
	 * Use {@link GeoEntityRenderer#getOverlay(T, float)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated()
	public int getPackedOverlay(LivingEntity entity, float u) {
		return this.getOverlay(animatable, u);
	}

	protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw,
			float partialTicks) {
		Pose pose = entityLiving.getPose();
		if (pose != Pose.SLEEPING) {
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
		}

		if (entityLiving.deathTime > 0) {
			float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
		} else if (entityLiving.isAutoSpinAttack()) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.xRot));
			matrixStackIn
					.mulPose(Vector3f.YP.rotationDegrees(((float) entityLiving.tickCount + partialTicks) * -75.0F));
		} else if (pose == Pose.SLEEPING) {
			Direction direction = entityLiving.getBedOrientation();
			float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270.0F));
		} else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity) {
			String s = TextFormatting.stripFormatting(entityLiving.getName().getString());
			if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity)
					|| ((PlayerEntity) entityLiving).isModelPartShown(PlayerModelPart.CAPE))) {
				matrixStackIn.translate(0.0D, (double) (entityLiving.getBbHeight() + 0.1F), 0.0D);
				matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
			}
		}

	}

	protected boolean isVisible(T livingEntityIn) {
		return !livingEntityIn.isInvisible();
	}

	private static float getFacingAngle(Direction facingIn) {
		switch (facingIn) {
		case SOUTH:
			return 90.0F;
		case WEST:
			return 0.0F;
		case NORTH:
			return 270.0F;
		case EAST:
			return 180.0F;
		default:
			return 0.0F;
		}
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn) {
		return 90.0F;
	}

	@Override
	public boolean shouldShowName(T entity) {
		double d0 = this.entityRenderDispatcher.distanceToSqr(entity);
		float f = entity.isDiscrete() ? 32.0F : 64.0F;
		if (d0 >= (double) (f * f)) {
			return false;
		} else {
			return entity == this.entityRenderDispatcher.crosshairPickEntity && entity.hasCustomName()
					&& Minecraft.renderNames();
		}
	}

	/**
	 * Returns where in the swing animation the living entity is (from 0 to 1). Args
	 * : entity, partialTickTime
	 */
	protected float getSwingProgress(T livingBase, float partialTickTime) {
		return livingBase.getAttackAnim(partialTickTime);
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(T livingBase, float partialTicks) {
		return (float) livingBase.tickCount + partialTicks;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	public final boolean addLayer(GeoLayerRenderer<T> layer) {
		return this.layerRenderers.add(layer);
	}

	public <E extends Entity> void renderLeash(T entity, float partialTicks, MatrixStack poseStack,
			IRenderTypeBuffer buffer, E leashHolder) {
		int u;
		poseStack.pushPose();
		Vector3d vec3d = leashHolder.getRopeHoldPosition(partialTicks);
		double d = (double) (MathHelper.lerp(partialTicks, entity.yBodyRot, entity.yBodyRotO) * ((float) Math.PI / 180))
				+ 1.5707963267948966;
		Vector3d vec3d2 = ((Entity) entity).getLeashOffset();
		double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
		double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
		double g = MathHelper.lerp(partialTicks, entity.xo, entity.getX()) + e;
		double h = MathHelper.lerp(partialTicks, entity.yo, entity.getY()) + vec3d2.y;
		double i = MathHelper.lerp(partialTicks, entity.zo, entity.getZ()) + f;
		poseStack.translate(e, vec3d2.y, f);
		float j = (float) (vec3d.x - g);
		float k = (float) (vec3d.y - h);
		float l = (float) (vec3d.z - i);
		IVertexBuilder vertexConsumer = buffer.getBuffer(RenderType.leash());
		Matrix4f matrix4f = poseStack.last().pose();
		float n = MathHelper.fastInvSqrt(j * j + l * l) * 0.025f / 2.0f;
		float o = l * n;
		float p = j * n;
		BlockPos blockPos = new BlockPos(entity.getEyePosition(partialTicks));
		BlockPos blockPos2 = new BlockPos(leashHolder.getEyePosition(partialTicks));
		int q = this.getBlockLightLevel(entity, blockPos);
		int r = leashHolder.isOnFire() ? 15 : leashHolder.level.getBrightness(LightType.BLOCK, blockPos2);
		int s = entity.level.getBrightness(LightType.SKY, blockPos);
		int t = entity.level.getBrightness(LightType.SKY, blockPos2);
		for (u = 0; u <= 24; ++u) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.025f, o, p, u,
					false);
		}
		for (u = 24; u >= 0; --u) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.0f, o, p, u,
					true);
		}
		poseStack.popPose();
	}

	private static void renderLeashPiece(IVertexBuilder vertexConsumer, Matrix4f positionMatrix, float f, float g,
			float h, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight,
			int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot) {
		float m = (float) pieceIndex / 24.0f;
		int n = (int) MathHelper.lerp(m, leashedEntityBlockLight, holdingEntityBlockLight);
		int o = (int) MathHelper.lerp(m, leashedEntitySkyLight, holdingEntitySkyLight);
		int p = LightTexture.pack(n, o);
		float q = pieceIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1.0f;
		float r = 0.5f * q;
		float s = 0.4f * q;
		float t = 0.3f * q;
		float u = f * m;
		float v = g > 0.0f ? g * m * m : g - g * (1.0f - m) * (1.0f - m);
		float w = h * m;
		vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(r, s, t, 1.0f).uv2(p).endVertex();
		vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(r, s, t, 1.0f).uv2(p).endVertex();
	}

	@Override
	public void setCurrentRTB(IRenderTypeBuffer rtb) {
		this.rtb = rtb;
	}

	@Override
	public IRenderTypeBuffer getCurrentRTB() {
		return this.rtb;
	}
}
