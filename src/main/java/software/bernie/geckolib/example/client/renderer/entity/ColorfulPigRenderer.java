package software.bernie.geckolib.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.example.client.renderer.model.entity.ColorfulPigModel;
import software.bernie.geckolib.example.entity.EntityColorfulPig;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ColorfulPigRenderer extends MobRenderer<EntityColorfulPig, ColorfulPigModel>
{
	public ColorfulPigRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new ColorfulPigModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(EntityColorfulPig entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/colorfulpig.png");
	}

	@Override
	protected void applyRotations(EntityColorfulPig entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}