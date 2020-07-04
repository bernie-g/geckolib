/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;


import com.mojang.blaze3d.platform.GlStateManager;
import software.bernie.geckolib.example.client.renderer.model.StingrayModel;
import software.bernie.geckolib.example.entity.StingrayTestEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StingrayRenderer extends MobEntityRenderer<StingrayTestEntity, StingrayModel>
{
	public StingrayRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new StingrayModel(), 0.5F);
	}

	@Override
	public Identifier getTexture(StingrayTestEntity entity)
	{
		return new Identifier("geckolib" +  ":textures/model/entity/stingray.png");
	}

	@Override
	protected void setupTransforms(StingrayTestEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}

	@Override
	public void render(StingrayTestEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
		GlStateManager.pushMatrix();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		GlStateManager.popMatrix();
	}
}