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
import software.bernie.geckolib.example.client.renderer.model.AscendedLegfishModel;
import software.bernie.geckolib.example.entity.AscendedLegfishEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class AscendedLegfishRenderer extends MobRenderer<AscendedLegfishEntity, AscendedLegfishModel>
{
	public AscendedLegfishRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new AscendedLegfishModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(AscendedLegfishEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/ascended_leg_fish.png");
	}

	@Override
	protected void applyRotations(AscendedLegfishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}