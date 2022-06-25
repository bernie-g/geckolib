package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
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
import software.bernie.geckolib3.util.RenderUtils;

@SuppressWarnings("unchecked")
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
	protected final List<GeoLayerRenderer<T>> layerRenderers = Lists.newArrayList();
	private Matrix4f renderEarlyMat = new Matrix4f();

	public ItemStack mainHand;
	public ItemStack offHand;
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public MultiBufferSource rtb;
	public ResourceLocation whTexture;

	public GeoEntityRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@SuppressWarnings("resource")
	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn,
			int packedLightIn) {
		stack.pushPose();
		boolean shouldSit = entity.isPassenger()
				&& (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = entity.isBaby();

		float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		float f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
		float netHeadYaw = f1 - f;
		if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entity.getVehicle();
			f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
			netHeadYaw = f1 - f;
			float f3 = Mth.wrapDegrees(netHeadYaw);
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

		float headPitch = Mth.lerp(partialTicks, entity.getXRot(), entity.getXRot());
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
			limbSwingAmount = Mth.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
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
			((IAnimatableModel<T>) modelProvider).setLivingAnimations(entity, this.getUniqueID(entity), predicate);
		}

		stack.translate(0, 0.01f, 0);
		RenderSystem.setShaderTexture(0, getTextureLocation(entity));
		Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn,
				getTextureLocation(entity));
		if (!entity.isInvisibleTo(Minecraft.getInstance().player))
			render(model, entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn,
					getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f,
					(float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
					(float) renderColor.getAlpha() / 255);

		if (!entity.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				this.renderLayer(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, f7,
						netHeadYaw, headPitch, bufferIn, layerRenderer);
			}
		}
		if (entity instanceof Mob) {
			Entity leashHolder = ((Mob) entity).getLeashHolder();
			if (leashHolder != null) {
				this.renderLeash(entity, partialTicks, stack, bufferIn, leashHolder);
			}
		}
		if (ModList.get().isLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	protected void renderLayer(PoseStack stack, MultiBufferSource bufferIn, int packedLightIn, T entity,
			float limbSwing, float limbSwingAmount, float partialTicks, float rotFloat, float netHeadYaw,
			float headPitch, MultiBufferSource bufferIn2, GeoLayerRenderer<T> layerRenderer) {
		layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, rotFloat,
				netHeadYaw, headPitch);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUUID().hashCode();
	}

	@Override
	public void renderEarly(T animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		renderEarlyMat = stackIn.last().pose().copy();
		this.mainHand = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
		this.offHand = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
		this.helmet = animatable.getItemBySlot(EquipmentSlot.HEAD);
		this.chestplate = animatable.getItemBySlot(EquipmentSlot.CHEST);
		this.leggings = animatable.getItemBySlot(EquipmentSlot.LEGS);
		this.boots = animatable.getItemBySlot(EquipmentSlot.FEET);
		this.rtb = renderTypeBuffer;
		this.whTexture = this.getTextureLocation(animatable);
		IGeoRenderer.super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, partialTicks);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
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
			PoseStack.Pose entry = stack.last();
			Matrix4f matBone = entry.pose().copy();
			bone.setWorldSpaceXform(matBone.copy());

			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			matBone.multiplyBackward(renderEarlyMatInvert);
			bone.setModelSpaceXform(matBone);
		}
		RenderUtils.moveBackFromPivot(bone, stack);

		if (!bone.isHidden) {
			Iterator<?> var10 = bone.childCubes.iterator();

			while (var10.hasNext()) {
				GeoCube cube = (GeoCube) var10.next();
				stack.pushPose();
				this.renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				stack.popPose();
			}

			var10 = bone.childBones.iterator();

			while (var10.hasNext()) {
				GeoBone childBone = (GeoBone) var10.next();
				this.renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
						alpha);
			}
		}

		stack.popPose();
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
		return OverlayTexture.pack(OverlayTexture.u(uIn),
				OverlayTexture.v(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
	}

	protected void applyRotations(T entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw,
			float partialTicks) {
		Pose pose = entityLiving.getPose();
		if (pose != Pose.SLEEPING) {
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
		}

		if (entityLiving.deathTime > 0) {
			float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f = Mth.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
		} else if (entityLiving.isAutoSpinAttack()) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
			matrixStackIn
					.mulPose(Vector3f.YP.rotationDegrees(((float) entityLiving.tickCount + partialTicks) * -75.0F));
		} else if (pose == Pose.SLEEPING) {
			Direction direction = entityLiving.getBedOrientation();
			float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270.0F));
		} else if (entityLiving.hasCustomName() || entityLiving instanceof Player) {
			String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
			if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof Player)
					|| ((Player) entityLiving).isModelPartShown(PlayerModelPart.CAPE))) {
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
			return entity == this.entityRenderDispatcher.crosshairPickEntity && entity.hasCustomName();
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

	private <E extends Entity> void renderLeash(T entity, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, E leashHolder) {
		poseStack.pushPose();
		Vec3 vec3 = leashHolder.getRopeHoldPosition(partialTicks);
		double d0 = (double) (Mth.lerp(partialTicks, entity.yBodyRot, entity.yBodyRotO) * ((float) Math.PI / 180F))
				+ (Math.PI / 2D);
		Vec3 vec31 = entity.getLeashOffset();
		double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
		double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
		double d3 = Mth.lerp(partialTicks, entity.xo, entity.getX()) + d1;
		double d4 = Mth.lerp(partialTicks, entity.yo, entity.getY()) + vec31.y;
		double d5 = Mth.lerp(partialTicks, entity.zo, entity.getZ()) + d2;
		poseStack.translate(d1, vec31.y, d2);
		float f = (float) (vec3.x - d3);
		float f1 = (float) (vec3.y - d4);
		float f2 = (float) (vec3.z - d5);
		VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.leash());
		Matrix4f matrix4f = poseStack.last().pose();
		float f4 = Mth.fastInvSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
		float f5 = f2 * f4;
		float f6 = f * f4;
		BlockPos blockpos = new BlockPos(entity.getEyePosition(partialTicks));
		BlockPos blockpos1 = new BlockPos(leashHolder.getEyePosition(partialTicks));
		int i = this.getBlockLightLevel(entity, blockpos);
		int j = this.getLeashHolderBlockLightLevel(leashHolder, blockpos1);
		int k = entity.level.getBrightness(LightLayer.SKY, blockpos);
		int l = entity.level.getBrightness(LightLayer.SKY, blockpos1);

		for (int i1 = 0; i1 <= 24; ++i1) {
			addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
		}

		for (int j1 = 24; j1 >= 0; --j1) {
			addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
		}

		poseStack.popPose();
	}

	private int getLeashHolderBlockLightLevel(Entity leashHolder, BlockPos pos) {
		return leashHolder.isOnFire() ? 15 : leashHolder.level.getBrightness(LightLayer.BLOCK, pos);
	}

	private static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix, float xDiff, float yDiff,
			float zDiff, int entityLightLevel, int holderLightLevel, int entitySkyLight, int holderSkyLight,
			float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
		float f = (float) p_174321_ / 24.0F;
		int i = (int) Mth.lerp(f, (float) entityLightLevel, (float) holderLightLevel);
		int j = (int) Mth.lerp(f, (float) entitySkyLight, (float) holderSkyLight);
		int k = LightTexture.pack(i, j);
		float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
		float f2 = 0.5F * f1;
		float f3 = 0.4F * f1;
		float f4 = 0.3F * f1;
		float f5 = xDiff * f;
		float f6 = yDiff > 0.0F ? yDiff * f * f : yDiff - yDiff * (1.0F - f) * (1.0F - f);
		float f7 = zDiff * f;
		vertexConsumer.vertex(matrix, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k)
				.endVertex();
		vertexConsumer.vertex(matrix, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_)
				.color(f2, f3, f4, 1.0F).uv2(k).endVertex();
	}
}
