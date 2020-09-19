package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.example.client.renderer.model.entity.RobotModel;
import software.bernie.example.entity.RobotEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RobotRenderer extends MobRenderer<RobotEntity, RobotModel>
{
	public RobotRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new RobotModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(RobotEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/robot.png");
	}

	@Override
	protected void applyRotations(RobotEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}