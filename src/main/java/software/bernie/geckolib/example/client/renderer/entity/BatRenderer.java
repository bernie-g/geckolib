package software.bernie.geckolib.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.example.client.renderer.model.entity.BatModel;
import software.bernie.geckolib.example.entity.BatEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BatRenderer extends MobRenderer<BatEntity, BatModel>
{
	public BatRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new BatModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(BatEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/bat.png");
	}

	@Override
	protected void applyRotations(BatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}