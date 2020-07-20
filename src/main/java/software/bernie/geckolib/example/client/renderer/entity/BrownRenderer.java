package software.bernie.geckolib.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.example.client.renderer.model.BrownModel;
import software.bernie.geckolib.example.entity.BrownEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BrownRenderer extends MobRenderer<BrownEntity, BrownModel>
{
	public BrownRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new BrownModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(BrownEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/brown.png");
	}

	@Override
	protected void applyRotations(BrownEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}