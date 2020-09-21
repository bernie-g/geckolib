package software.bernie.geckolib.renderers.geo;

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
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.core.IAnimatableModel;
import software.bernie.geckolib.model.provider.data.EntityModelData;
import software.bernie.geckolib.util.AnimationUtils;

import java.awt.*;
import java.util.List;

public abstract class GeoEntityRenderer<T extends LivingEntity & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T>
{
	static
	{
		AnimationController.addModelFetcher((Object object) ->
		{
			if (object instanceof Entity)
			{
				return (IAnimatableModel) AnimationUtils.getGeoModelForEntity((Entity) object);
			}
			return null;
		});
	}

	private final AnimatedGeoModel<T> modelProvider;
	protected final List<GeoLayerRenderer<T>> layerRenderers = Lists.newArrayList();

	protected GeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider)
	{
		super(renderManager);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		EntityModelData entityModelData = getOrCreateEntityModelData();
		entityModelData.isSitting = shouldSit;
		entityModelData.isChild = entity.isChild();
		float f = MathHelper.interpolateAngle(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
		float f1 = MathHelper.interpolateAngle(partialTicks, entity.prevRotationYawHead, entity.rotationYawHead);
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
			Direction direction = entity.getBedDirection();
			if (direction != null)
			{
				float f4 = entity.getEyeHeight(Pose.STANDING) - 0.1F;
				stack.translate((double) ((float) (-direction.getXOffset()) * f4), 0.0D, (double) ((float) (-direction.getZOffset()) * f4));
			}
		}
		float f7 = this.handleRotationFloat(entity, partialTicks);
		this.applyRotations(entity, stack, f7, f, partialTicks);

		float limbSwingAmount = 0.0F;
		float limbSwing = 0.0F;
		if (!shouldSit && entity.isAlive())
		{
			limbSwingAmount = MathHelper.lerp(partialTicks, entity.prevLimbSwingAmount, entity.limbSwingAmount);
			limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
			if (entity.isChild())
			{
				limbSwing *= 3.0F;
			}

			if (limbSwingAmount > 1.0F)
			{
				limbSwingAmount = 1.0F;
			}
		}
		AnimationTestPredicate predicate = new AnimationTestPredicate(entity, limbSwing, limbSwingAmount, partialTicks, !(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F));

		if (modelProvider instanceof IAnimatableModel)
		{
			((IAnimatableModel<T>) modelProvider).setLivingAnimations(entity, predicate);
		}

		stack.push();
		stack.translate(0, 0.01f, 0);
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entity));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entity));
		Color renderColor = getRenderColor(entity, partialTicks, stack, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(entity, partialTicks, stack, bufferIn, packedLightIn, getEntityTexture(entity));
		render(model, entity, partialTicks, renderType, stack, bufferIn, packedLightIn, getPackedOverlay(entity, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getAlpha() / 255);

		if (!entity.isSpectator())
		{
			for (GeoLayerRenderer<T> layerRenderer : this.layerRenderers)
			{
				layerRenderer.render(stack, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, f7, f2, f6);
			}
		}
		stack.pop();
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	private EntityModelData getOrCreateEntityModelData()
	{
		EntityModelData modelData = this.modelProvider.getModelData(EntityModelData.class);
		if (modelData == null)
		{
			this.modelProvider.putModelData(EntityModelData.class, new EntityModelData());
		}
		return modelData;
	}

	@Override
	public ResourceLocation getEntityTexture(T entity)
	{
		return modelProvider.getTextureLocation(entity);
	}

	@Override
	public GeoModelProvider getGeoModelProvider()
	{
		return this.modelProvider;
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn)
	{
		return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
	}

	protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
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

	protected boolean isVisible(T livingEntityIn)
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

	protected float getDeathMaxRotation(T entityLivingBaseIn)
	{
		return 90.0F;
	}

	/**
	 * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
	 */
	protected float getSwingProgress(T livingBase, float partialTickTime)
	{
		return livingBase.getSwingProgress(partialTickTime);
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(T livingBase, float partialTicks)
	{
		return (float) livingBase.ticksExisted + partialTicks;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance)
	{
		return this.modelProvider.getTextureLocation(instance);
	}

	public final boolean addLayer(GeoLayerRenderer<T> layer)
	{
		return this.layerRenderers.add(layer);
	}
}
