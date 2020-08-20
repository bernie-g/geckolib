package software.bernie.geckolib.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.example.client.renderer.model.entity.LightCrystalModel;
import software.bernie.geckolib.example.entity.LightCrystalEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class LightCrystalRenderer extends MobRenderer<LightCrystalEntity, LightCrystalModel>
{
	public LightCrystalRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new LightCrystalModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(LightCrystalEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/lightcrystal.png");
	}

	@Override
	protected void applyRotations(LightCrystalEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}