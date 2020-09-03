package software.bernie.geckolib.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.example.client.renderer.model.entity.EntityBotariumModel;
import software.bernie.geckolib.example.client.renderer.model.entity.RobotModel;
import software.bernie.geckolib.example.entity.EntityBotarium;
import software.bernie.geckolib.example.entity.RobotEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class EntityBotariumRenderer extends MobRenderer<EntityBotarium, EntityBotariumModel>
{
	public EntityBotariumRenderer(EntityRendererManager rendererManager)
	{
		super(rendererManager, new EntityBotariumModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(EntityBotarium entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/botarium.png");
	}

	@Override
	protected void applyRotations(EntityBotarium entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}