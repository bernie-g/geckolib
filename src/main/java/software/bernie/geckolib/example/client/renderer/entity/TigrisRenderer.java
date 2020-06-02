/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.example.client.renderer.model.TigrisModel;
import software.bernie.geckolib.example.entity.TigrisEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class TigrisRenderer extends MobRenderer<TigrisEntity, TigrisModel>
{
	public TigrisRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new TigrisModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(TigrisEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/tigris.png");
	}

	@Override
	protected void applyRotations(TigrisEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}