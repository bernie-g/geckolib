/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;


import software.bernie.geckolib.example.client.renderer.model.AscendedLegfishModel;
import software.bernie.geckolib.example.entity.AscendedLegfishEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AscendedLegfishRenderer extends MobEntityRenderer<AscendedLegfishEntity, AscendedLegfishModel>
{
	public AscendedLegfishRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new AscendedLegfishModel(), 0.5F);
	}

	@Override
	public Identifier getTexture(AscendedLegfishEntity entity)
	{
		return new Identifier("geckolib" + ":textures/model/entity/ascended_leg_fish.png");
	}

	@Override
	protected void setupTransforms(AscendedLegfishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}