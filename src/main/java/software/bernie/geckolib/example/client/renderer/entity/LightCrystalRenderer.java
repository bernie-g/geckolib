package software.bernie.geckolib.example.client.renderer.entity;

import software.bernie.geckolib.example.client.renderer.model.LightCrystalModel;
import software.bernie.geckolib.example.entity.LightCrystalEntity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LightCrystalRenderer extends MobEntityRenderer<LightCrystalEntity, LightCrystalModel>
{
	public LightCrystalRenderer(EntityRenderDispatcher rendererManager)
	{
		super(rendererManager, new LightCrystalModel(), 0.5F);
	}

	@Nullable
	@Override
	public Identifier getTexture(LightCrystalEntity entity)
	{
		return new Identifier("geckolib" + ":textures/model/entity/lightcrystal.png");
	}

	@Override
	protected void setupTransforms(LightCrystalEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
	{
		super.setupTransforms(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
	}
}