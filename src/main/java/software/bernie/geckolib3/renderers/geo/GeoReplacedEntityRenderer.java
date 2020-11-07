package software.bernie.geckolib3.renderers.geo;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeoReplacedEntityRenderer<T extends IAnimatable> extends EntityRenderer implements IGeoRenderer
{
	private final AnimatedGeoModel<IAnimatable> modelProvider;
	private final T animatable;
	protected final List<GeoLayerRenderer> layerRenderers = Lists.newArrayList();
	private IAnimatable currentAnimatable;
	private static Map<Class<? extends IAnimatable>, GeoReplacedEntityRenderer> renderers = new ConcurrentHashMap<>();

	static
	{
		AnimationController.addModelFetcher((IAnimatable object) ->
		{
			GeoReplacedEntityRenderer renderer = renderers.get(object.getClass());
			return renderer == null ? null : renderer.getGeoModelProvider();
		});
	}

	protected GeoReplacedEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<IAnimatable> modelProvider, T animatable)
	{
		super(renderManager);
		this.modelProvider = modelProvider;
		this.animatable = animatable;
	}

	public static void registerReplacedEntity(Class<? extends IAnimatable> itemClass, GeoReplacedEntityRenderer renderer)
	{
		renderers.put(itemClass, renderer);
	}

	public static GeoReplacedEntityRenderer getRenderer(Class<? extends IAnimatable> item)
	{
		return renderers.get(item);
	}

	@Override
	public void render(Entity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		this.render(entityIn, this.animatable, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	public void render(Entity entity, IAnimatable animatable, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		this.currentAnimatable = animatable;
		LivingEntity entityLiving;
		if (entity instanceof LivingEntity)
		{
			entityLiving = (LivingEntity) entity;
		}
		else
		{
			throw (new RuntimeException("Replaced renderer was not an instanceof LivingEntity"));
		}

		boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		EntityModelData entityModelData = new EntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = entityLiving.isChild();

		float f = MathHelper.interpolateAngle(partialTicks, entityLiving.prevRenderYawOffset, entityLiving.renderYawOffset);
		float f1 = MathHelper.interpolateAngle(partialTicks, entityLiving.prevRotationYawHead, entityLiving.rotationYawHead);
		float f2 = f1 - f;
		if (shouldSit && entity.getRidingEntity() instanceof LivingEntity)
		{
			LivingEntity livingentity = (LivingEntity) entity.getRidingEntity();
			f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
			f2 = f1 - f;
			float f3 = MathHelper.wrapDegrees(f2);
			if (f3 < -85.0F)
			{
				f3 = -85.0F;
			}

			if (f3 >= 85.0F)
			{
				f3 = 85.0F;
			}

			f = f1 - f3;
			if (f3 * f3 > 2500.0F)
			{
				f += f3 * 0.2F;
			}

			f2 = f1 - f;
		}

		float f6 = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
		if (entity.getPose() == Pose.SLEEPING)
		{
			Direction direction = entityLiving.getBedDirection();
			if (direction != null)
			{
				float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				stack.translate((double) ((float) (-direction.getXOffset()) * f4), 0.0D, (double) ((float) (-direction.getZOffset()) * f4));
			}
		}
		float f7 = this.handleRotationFloat(entityLiving, partialTicks);
		this.applyRotations(entityLiving, stack, f7, f, partialTicks);
		this.preRenderCallback(entityLiving, stack, partialTicks);

		float limbSwingAmount = 0.0F;
		float limbSwing = 0.0F;
		if (!shouldSit && entity.isAlive())
		{
			limbSwingAmount = MathHelper.lerp(partialTicks, entityLiving.prevLimbSwingAmount, entityLiving.limbSwingAmount);
			limbSwing = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks);
			if (entityLiving.isChild())
			{
				limbSwing *= 3.0F;
			}

			if (limbSwingAmount > 1.0F)
			{
				limbSwingAmount = 1.0F;
			}
		}

		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
		AnimationEvent predicate = new AnimationEvent(animatable, limbSwing, limbSwingAmount, partialTicks, !(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F), Collections.singletonList(entityModelData));
		if (modelProvider instanceof IAnimatableModel)
		{
			((IAnimatableModel) modelProvider).setLivingAnimations(animatable, this.getUniqueID(entity), predicate);
		}

		stack.push();
		stack.translate(0, 0.01f, 0);
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entity));
		Color renderColor = getRenderColor(animatable, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(entity, partialTicks, stack, bufferIn, null, packedLightIn, getEntityTexture(entity));
		render(model, entity, partialTicks, renderType, stack, bufferIn, null, packedLightIn, getPackedOverlay(entityLiving, this.getOverlayProgress(entityLiving, partialTicks)), (float) renderColor.getRed() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getAlpha() / 255);

		if (!entity.isSpectator())
		{
			for (GeoLayerRenderer layerRenderer : this.layerRenderers)
			{
				layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, f7, f2, f6);
			}
		}
		stack.pop();
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	protected float getOverlayProgress(LivingEntity livingEntityIn, float partialTicks)
	{
		return 0.0F;
	}

	protected void preRenderCallback(LivingEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
	{
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity)
	{
		return modelProvider.getTextureLocation(currentAnimatable);
	}

	@Override
	public AnimatedGeoModel getGeoModelProvider()
	{
		return this.modelProvider;
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn)
	{
		return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
	}

	protected void applyRotations(LivingEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		Pose pose = entityLiving.getPose();
		if (pose != Pose.SLEEPING)
		{
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
		}

		if (entityLiving.deathTime > 0)
		{
			float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F)
			{
				f = 1.0F;
			}

			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
		}
		else if (entityLiving.isSpinAttacking())
		{
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.rotationPitch));
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(((float) entityLiving.ticksExisted + partialTicks) * -75.0F));
		}
		else if (pose == Pose.SLEEPING)
		{
			Direction direction = entityLiving.getBedDirection();
			float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
			matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0F));
		}
		else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity)
		{
			String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());
			if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity) entityLiving).isWearing(PlayerModelPart.CAPE)))
			{
				matrixStackIn.translate(0.0D, (double) (entityLiving.getHeight() + 0.1F), 0.0D);
				matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
			}
		}

	}

	protected boolean isVisible(LivingEntity livingEntityIn)
	{
		return !livingEntityIn.isInvisible();
	}

	private static float getFacingAngle(Direction facingIn)
	{
		switch (facingIn)
		{
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

	protected float getDeathMaxRotation(LivingEntity entityLivingBaseIn)
	{
		return 90.0F;
	}

	/**
	 * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
	 */
	protected float getSwingProgress(LivingEntity livingBase, float partialTickTime)
	{
		return livingBase.getSwingProgress(partialTickTime);
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(LivingEntity livingBase, float partialTicks)
	{
		return (float) livingBase.ticksExisted + partialTicks;
	}

	@Override
	public ResourceLocation getTextureLocation(Object instance)
	{
		return this.modelProvider.getTextureLocation((IAnimatable) instance);
	}

	public final boolean addLayer(GeoLayerRenderer<? extends LivingEntity> layer)
	{
		return this.layerRenderers.add(layer);
	}
}
