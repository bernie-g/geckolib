package software.bernie.geckolib.example.client.renderer.entity;

import software.bernie.geckolib.example.client.renderer.model.EasingDemoModel;
import software.bernie.geckolib.example.entity.EasingDemoEntity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EasingDemoRenderer extends MobEntityRenderer<EasingDemoEntity, EasingDemoModel>
{
	public EasingDemoRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new EasingDemoModel(), 0.5F);
	}

	@Nullable
	@Override
	public Identifier getTexture(EasingDemoEntity entity)
	{
		return new Identifier("geckolib" + ":textures/model/entity/brown.png");
	}

	@Override
	protected void setupTransforms(EasingDemoEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}