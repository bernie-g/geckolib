package software.bernie.geckolib3.renderers.geo;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.util.AnimationUtils;

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

	protected final List<GeoLayerRenderer<T>> layerRenderers = Lists.newArrayList();
	private final AnimatedGeoModel<T> modelProvider;

	public ItemStack mainHand;
	public ItemStack offHand;
	public ItemStack helmet;
	public ItemStack chestplate;
	public ItemStack leggings;
	public ItemStack boots;
	public VertexConsumerProvider rtb;
	public Identifier whTexture;

	protected GeoEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<T> modelProvider) {
		super(ctx);
		this.modelProvider = modelProvider;
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

	@SuppressWarnings("resource")
	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack,
			VertexConsumerProvider bufferIn, int packedLightIn) {
		stack.push();
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

		float headPitch = MathHelper.lerp(partialTicks, entity.prevPitch, entity.getPitch());
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
		boolean invis = entity.isInvisibleTo(MinecraftClient.getInstance().player);
		render(model, entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn,
				getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, invis ? 0.0F : (float) renderColor.getAlpha() / 255);

		if (!entity.isSpectator()) {
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers) {
				layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, lastLimbDistance, partialTicks,
						f7, netHeadYaw, headPitch);
			}
		}
		if (FabricLoader.getInstance().isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.pop();
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return animatable.getUuid().hashCode();
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
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
	public Identifier getTexture(T entity) {
		return getTextureLocation(entity);
	}

	@Override
	public GeoModelProvider<T> getGeoModelProvider() {
		return this.modelProvider;
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
			return entity == this.dispatcher.targetedEntity && entity.hasCustomName();
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
}
