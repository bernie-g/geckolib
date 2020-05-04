package software.bernie.geckolib.client.renderer.entity;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.client.renderer.model.StingrayModel;
import software.bernie.geckolib.entity.StingrayTestEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class StingrayRenderer extends MobRenderer<StingrayTestEntity, StingrayModel>
{
	public StingrayRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new StingrayModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(StingrayTestEntity entity)
	{
		return new ResourceLocation("geckolib" +  ":textures/model/entity/stingray.png");
	}

	@Override
	protected void applyRotations(StingrayTestEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}

	@Override
	public void render(StingrayTestEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		GlStateManager.pushMatrix();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		GlStateManager.popMatrix();
	}
}