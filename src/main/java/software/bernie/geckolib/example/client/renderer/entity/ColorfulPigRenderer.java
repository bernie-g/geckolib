package software.bernie.geckolib.example.client.renderer.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.example.client.renderer.model.BrownModel;
import software.bernie.geckolib.example.client.renderer.model.ColorfulPigModel;
import software.bernie.geckolib.example.entity.BrownEntity;
import software.bernie.geckolib.example.entity.EntityColorfulPig;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ColorfulPigRenderer extends MobEntityRenderer<EntityColorfulPig, ColorfulPigModel>
{
	public ColorfulPigRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new ColorfulPigModel(), 0.5F);
	}

	@Nullable
	@Override
	public Identifier getTexture(EntityColorfulPig entity)
	{
		return new Identifier("geckolib" + ":textures/model/entity/colorfulpig.png");
	}

	@Override
	protected void setupTransforms(EntityColorfulPig entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}
