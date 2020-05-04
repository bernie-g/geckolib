package software.bernie.geckolib.client.renderer.entity;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.client.renderer.model.TurretModel;
import software.bernie.geckolib.entity.TurretEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class TurretRenderer extends MobRenderer<TurretEntity, TurretModel>
{
	public TurretRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new TurretModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(TurretEntity entity)
	{
		return new ResourceLocation("deepwaters" + ":textures/model/entity/sdlfsdf.png");
	}

	@Override
	protected void applyRotations(TurretEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}