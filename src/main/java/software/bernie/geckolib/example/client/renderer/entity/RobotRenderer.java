package software.bernie.geckolib.example.client.renderer.entity;

import software.bernie.geckolib.example.client.renderer.model.BrownModel;
import software.bernie.geckolib.example.client.renderer.model.RobotModel;
import software.bernie.geckolib.example.entity.BrownEntity;
import software.bernie.geckolib.example.entity.RobotEntity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RobotRenderer extends MobEntityRenderer<RobotEntity, RobotModel>
{
	public RobotRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new RobotModel(), 0.5F);
	}

	@Nullable
	@Override
	public Identifier getTexture(RobotEntity entity)
	{
		return new Identifier("geckolib" + ":textures/model/entity/robot.png");
	}

	@Override
	protected void setupTransforms(RobotEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}