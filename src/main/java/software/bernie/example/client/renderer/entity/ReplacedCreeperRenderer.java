package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.MathHelper;
import software.bernie.example.client.model.entity.ReplacedCreeperModel;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

public class ReplacedCreeperRenderer extends GeoReplacedEntityRenderer<ReplacedCreeperEntity> {
	@SuppressWarnings("unchecked")
	public ReplacedCreeperRenderer(RenderManager renderManager) {
		super(renderManager, new ReplacedCreeperModel(), new ReplacedCreeperEntity());
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
		EntityCreeper creeper = (EntityCreeper) entitylivingbaseIn;
		float f = creeper.getCreeperFlashIntensity(partialTickTime);
		float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		f = f * f;
		f = f * f;
		float f2 = (1.0F + f * 0.4F) * f1;
		float f3 = (1.0F + f * 0.1F) / f1;
		GlStateManager.scale(f2, f3, f2);
	}
}
