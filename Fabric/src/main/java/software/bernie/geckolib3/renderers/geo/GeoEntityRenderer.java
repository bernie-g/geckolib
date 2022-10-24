package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

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
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Entity) {
				return (IAnimatableModel<Object>) AnimationUtils.getGeoModelForEntity((Entity) object);
			}
			return null;
		});
	}

	protected final List<GeoLayerRenderer<T>> layerRenderers = new ObjectArrayList<>();
	protected final AnimatedGeoModel<T> modelProvider;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;

	public ItemStack mainHand;
	public ItemStack offHand;
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public VertexConsumerProvider rtb;
	public Identifier whTexture;
	protected float widthScale = 1;
	protected float heightScale = 1;

	public GeoEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<T> modelProvider) {
		super(ctx);
		this.modelProvider = modelProvider;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
		return OverlayTexture.getUv(OverlayTexture.getU(uIn),
				livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0);
	}

	private static float getFacingAngle(Direction facingIn) {
		switch (facingIn) {
		case SOUTH:
			return 90.0F;
		case NORTH:
			return 270.0F;
		case EAST:
			return 180.0F;
		case WEST:
		default:
			return 0.0F;
		}
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack,
			VertexConsumerProvider bufferIn, int packedLightIn) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		stack.push();
		if (entity instanceof MobEntity) {
			Entity leashHolder = ((MobEntity) entity).getHoldingEntity();
			if (leashHolder != null) {
				this.renderLeash(entity, partialTicks, stack, bufferIn, leashHolder);
			}
		}
		this.dispatchedMat = stack.peek().getPositionMatrix().copy();
		boolean shouldSit = entity.hasVehicle() && (entity.getVehicle() != null);
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = entity.isBaby();

		float f = MathHelper.lerpAngleDegrees(partialTicks, entity.prevBodyYaw, entity.bodyYaw);
		float f1 = MathHelper.lerpAngleDegrees(partialTicks, entity.prevHeadYaw, entity.headYaw);
		float netHeadYaw = f1 - f;
		if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingentity = (LivingEntity) entity.getVehicle();
			f = MathHelper.lerpAngleDegrees(partialTicks, livingentity.prevBodyYaw, livingentity.bodyYaw);
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

		float headPitch = MathHelper.lerp(partialTicks, entity.getPitch(), entity.getPitch());
		if (entity.getPose() == EntityPose.SLEEPING) {
			Direction direction = entity.getSleepingDirection();
			if (direction != null) {
				float f4 = entity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				stack.translate((float) (-direction.getOffsetX()) * f4, 0.0D, (float) (-direction.getOffsetZ()) * f4);
			}
		}
		float f7 = this.handleRotationFloat(entity, partialTicks);
		this.applyRotations(entity, stack, f7, f, partialTicks);

		float lastLimbDistance = 0.0F;
		float limbSwing = 0.0F;
		if (!shouldSit && entity.isAlive()) {
			lastLimbDistance = MathHelper.lerp(partialTicks, entity.lastLimbDistance, entity.limbDistance);
			limbSwing = entity.limbAngle - entity.limbDistance * (1.0F - partialTicks);
			if (entity.isBaby()) {
				limbSwing *= 3.0F;
			}

			if (lastLimbDistance > 1.0F) {
				lastLimbDistance = 1.0F;
			}
		}
		entityModelData.headPitch = -headPitch;
		entityModelData.netHeadYaw = -netHeadYaw;

		AnimationEvent<T> predicate = new AnimationEvent<T>(entity, limbSwing, lastLimbDistance, partialTicks,
				!(lastLimbDistance > -0.15F && lastLimbDistance < 0.15F), Collections.singletonList(entityModelData));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entity));
		if (modelProvider instanceof IAnimatableModel) {
			((IAnimatableModel<T>) modelProvider).setLivingAnimations(entity, this.getUniqueID(entity), predicate);
		}

		stack.translate(0, 0.01f, 0);
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTexture(entity));
		Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderLayer renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn,
				getTexture(entity));
		if (!entity.isInvisibleTo(MinecraftClient.getInstance().player)) {
			VertexConsumer glintBuffer = bufferIn.getBuffer(RenderLayer.getDirectEntityGlint());
			VertexConsumer translucentBuffer = bufferIn
					.getBuffer(RenderLayer.getEntityTranslucentCull(getTextureLocation(entity)));
			render(model, entity, partialTicks, renderType, stack, bufferIn,
					glintBuffer != translucentBuffer ? VertexConsumers.union(glintBuffer, translucentBuffer) : null,
					packedLightIn, getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f,
					(float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
					(float) renderColor.getAlpha() / 255);
		}

		if (!entity.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				this.renderLayer(stack, bufferIn, packedLightIn, entity, limbSwing, lastLimbDistance, partialTicks, f7,
						netHeadYaw, headPitch, bufferIn, layerRenderer);
			}
		}
		if (FabricLoader.getInstance().isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.pop();
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	protected void renderLayer(MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn, T entity,
			float limbSwing, float limbSwingAmount, float partialTicks, float rotFloat, float netHeadYaw,
			float headPitch, VertexConsumerProvider bufferIn2, GeoLayerRenderer<T> layerRenderer) {
		layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, rotFloat,
				netHeadYaw, headPitch);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUuid().hashCode();
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		renderEarlyMat = stackIn.peek().getPositionMatrix().copy();
		this.animatable = animatable;
		this.mainHand = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
		this.offHand = animatable.getEquippedStack(EquipmentSlot.OFFHAND);
		this.helmet = animatable.getEquippedStack(EquipmentSlot.HEAD);
		this.chestplate = animatable.getEquippedStack(EquipmentSlot.CHEST);
		this.leggings = animatable.getEquippedStack(EquipmentSlot.LEGS);
		this.boots = animatable.getEquippedStack(EquipmentSlot.FEET);
		this.rtb = renderTypeBuffer;
		this.whTexture = this.getTextureLocation(animatable);
		IGeoRenderer.super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn,
				packedOverlayIn, red, green, blue, partialTicks);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		stack.push();
		boolean rotOverride = bone.rotMat != null;
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		if (rotOverride) {
			stack.peek().getPositionMatrix().multiply(bone.rotMat);
			stack.peek().getNormalMatrix().multiply(new Matrix3f(bone.rotMat));
		} else {
			RenderUtils.rotate(bone, stack);
		}
		RenderUtils.scale(bone, stack);
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.peek();
			Matrix4f boneMat = entry.getPositionMatrix().copy();

			// Model space
			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			Matrix4f modelPosBoneMat = boneMat.copy();
			multiplyBackward(modelPosBoneMat, renderEarlyMatInvert);
			bone.setModelSpaceXform(modelPosBoneMat);

			// Local space
			Matrix4f dispatchedMatInvert = this.dispatchedMat.copy();
			dispatchedMatInvert.invert();
			Matrix4f localPosBoneMat = boneMat.copy();
			multiplyBackward(localPosBoneMat, dispatchedMatInvert);
			// (Offset is the only transform we may want to preserve from the dispatched
			// mat)
			Vec3d renderOffset = this.getPositionOffset(animatable, 1.0F);
			localPosBoneMat.addToLastColumn(
					new Vec3f((float) renderOffset.getX(), (float) renderOffset.getY(), (float) renderOffset.getZ()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			worldPosBoneMat.addToLastColumn(
					new Vec3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			bone.setWorldSpaceXform(worldPosBoneMat);
		}
		RenderUtils.moveBackFromPivot(bone, stack);

		if (!bone.isHidden) {
			Iterator<?> var10 = bone.childCubes.iterator();

			while (var10.hasNext()) {
				GeoCube cube = (GeoCube) var10.next();
				stack.push();
				if (!bone.cubesAreHidden()) {
					this.renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				}
				stack.pop();
			}

			var10 = bone.childBones.iterator();

			while (var10.hasNext()) {
				GeoBone childBone = (GeoBone) var10.next();
				this.renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
						alpha);
			}
		}

		stack.pop();
	}

	public void multiplyBackward(Matrix4f first, Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.multiply(first);
		first.load(copy);
	}

	@Override
	public Identifier getTexture(T entity) {
		return getTextureLocation(entity);
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@Override
	public float getWidthScale(T entity) {
		return this.widthScale;
	}

	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw,
			float partialTicks) {
		EntityPose pose = entityLiving.getPose();
		if (pose != EntityPose.SLEEPING) {
			matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - rotationYaw));
		}

		if (entityLiving.deathTime > 0) {
			float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrixStackIn.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * this.getDeathMaxRotation(entityLiving)));
		} else if (entityLiving.isUsingRiptide()) {
			matrixStackIn.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F - entityLiving.getPitch()));
			matrixStackIn.multiply(
					Vec3f.POSITIVE_Y.getDegreesQuaternion(((float) entityLiving.age + partialTicks) * -75.0F));
		} else if (pose == EntityPose.SLEEPING) {
			Direction direction = entityLiving.getSleepingDirection();
			float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
			matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f1));
			matrixStackIn.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(this.getDeathMaxRotation(entityLiving)));
			matrixStackIn.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
		} else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity) {
			String s = Formatting.strip(entityLiving.getName().getString());
			if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity)
					|| ((PlayerEntity) entityLiving).isPartVisible(PlayerModelPart.CAPE))) {
				matrixStackIn.translate(0.0D, entityLiving.getHeight() + 0.1F, 0.0D);
				matrixStackIn.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			}
		}

	}

	@Override
	protected boolean hasLabel(T entity) {
		double d0 = this.dispatcher.getSquaredDistanceToCamera(entity);
		float f = entity.isSneaking() ? 32.0F : 64.0F;
		if (d0 >= (double) (f * f)) {
			return false;
		} else {
			return entity == this.dispatcher.targetedEntity && entity.hasCustomName() && MinecraftClient.isHudEnabled();
		}
	}

	protected boolean isVisible(T livingEntityIn) {
		return !livingEntityIn.isInvisible();
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn) {
		return 90.0F;
	}

	/**
	 * Returns where in the swing animation the living entity is (from 0 to 1). Args
	 * : entity, partialTickTime
	 */
	protected float getHandSwingProgress(T livingBase, float partialTickTime) {
		return livingBase.getHandSwingProgress(partialTickTime);
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(T livingBase, float partialTicks) {
		return (float) livingBase.age + partialTicks;
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	public final boolean addLayer(GeoLayerRenderer<T> layer) {
		return this.layerRenderers.add(layer);
	}

	public <E extends Entity> void renderLeash(T entity, float partialTicks, MatrixStack poseStack,
			VertexConsumerProvider buffer, E leashHolder) {
		int u;
		poseStack.push();
		Vec3d vec3d = leashHolder.getLeashPos(partialTicks);
		double d = (double) (MathHelper.lerp(partialTicks, entity.bodyYaw, entity.prevBodyYaw)
				* ((float) Math.PI / 180)) + 1.5707963267948966;
		Vec3d vec3d2 = ((Entity) entity).getLeashOffset();
		double e = Math.cos(d) * vec3d2.z + Math.sin(d) * vec3d2.x;
		double f = Math.sin(d) * vec3d2.z - Math.cos(d) * vec3d2.x;
		double g = MathHelper.lerp(partialTicks, entity.prevX, entity.getX()) + e;
		double h = MathHelper.lerp(partialTicks, entity.prevY, entity.getY()) + vec3d2.y;
		double i = MathHelper.lerp(partialTicks, entity.prevZ, entity.getZ()) + f;
		poseStack.translate(e, vec3d2.y, f);
		float j = (float) (vec3d.x - g);
		float k = (float) (vec3d.y - h);
		float l = (float) (vec3d.z - i);
		VertexConsumer vertexConsumer = buffer.getBuffer(RenderLayer.getLeash());
		Matrix4f matrix4f = poseStack.peek().getPositionMatrix();
		float n = MathHelper.fastInverseSqrt(j * j + l * l) * 0.025f / 2.0f;
		float o = l * n;
		float p = j * n;
		BlockPos blockPos = new BlockPos(entity.getCameraPosVec(partialTicks));
		BlockPos blockPos2 = new BlockPos(leashHolder.getCameraPosVec(partialTicks));
		int q = this.getBlockLight(entity, blockPos);
		int r = leashHolder.isOnFire() ? 15 : leashHolder.world.getLightLevel(LightType.BLOCK, blockPos2);
		int s = entity.world.getLightLevel(LightType.SKY, blockPos);
		int t = entity.world.getLightLevel(LightType.SKY, blockPos2);
		for (u = 0; u <= 24; ++u) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.025f, o, p, u,
					false);
		}
		for (u = 24; u >= 0; --u) {
			GeoEntityRenderer.renderLeashPiece(vertexConsumer, matrix4f, j, k, l, q, r, s, t, 0.025f, 0.0f, o, p, u,
					true);
		}
		poseStack.pop();
	}

	private static void renderLeashPiece(VertexConsumer vertexConsumer, Matrix4f positionMatrix, float f, float g,
			float h, int leashedEntityBlockLight, int holdingEntityBlockLight, int leashedEntitySkyLight,
			int holdingEntitySkyLight, float i, float j, float k, float l, int pieceIndex, boolean isLeashKnot) {
		float m = (float) pieceIndex / 24.0f;
		int n = (int) MathHelper.lerp(m, leashedEntityBlockLight, holdingEntityBlockLight);
		int o = (int) MathHelper.lerp(m, leashedEntitySkyLight, holdingEntitySkyLight);
		int p = LightmapTextureManager.pack(n, o);
		float q = pieceIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1.0f;
		float r = 0.5f * q;
		float s = 0.4f * q;
		float t = 0.3f * q;
		float u = f * m;
		float v = g > 0.0f ? g * m * m : g - g * (1.0f - m) * (1.0f - m);
		float w = h * m;
		vertexConsumer.vertex(positionMatrix, u - k, v + j, w + l).color(r, s, t, 1.0f).light(p).next();
		vertexConsumer.vertex(positionMatrix, u + k, v + i - j, w - l).color(r, s, t, 1.0f).light(p).next();
	}

	@Override
	public void setCurrentRTB(VertexConsumerProvider rtb) {
		this.rtb = rtb;
	}

	@Override
	public VertexConsumerProvider getCurrentRTB() {
		return this.rtb;
	}
}
