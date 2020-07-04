/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;

import software.bernie.geckolib.example.client.renderer.model.TigrisModel;
import software.bernie.geckolib.example.entity.TigrisEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TigrisRenderer extends MobEntityRenderer<TigrisEntity, TigrisModel>
{
	public TigrisRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new TigrisModel(), 0.5F);
	}

	@Override
	public Identifier getTexture(TigrisEntity entity)
	{
		return new Identifier("geckolib" + ":textures/model/entity/tigris.png");
	}

	@Override
	protected void setupTransforms(TigrisEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}