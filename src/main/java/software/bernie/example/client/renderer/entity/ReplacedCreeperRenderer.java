package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.util.Mth;
import software.bernie.example.client.model.entity.ReplacedCreeperModel;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

public class ReplacedCreeperRenderer extends GeoReplacedEntityRenderer<ReplacedCreeperEntity> {
	public ReplacedCreeperRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ReplacedCreeperModel(), new ReplacedCreeperEntity());
	}

	@Override
	protected void preRenderCallback(LivingEntity entitylivingbaseIn, PoseStack matrixStackIn,
			float partialTickTime) {
		Creeper creeper = (Creeper) entitylivingbaseIn;
		float f = creeper.getSwelling(partialTickTime);
		float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
		f = Mth.clamp(f, 0.0F, 1.0F);
		f = f * f;
		f = f * f;
		float f2 = (1.0F + f * 0.4F) * f1;
		float f3 = (1.0F + f * 0.1F) / f1;
		matrixStackIn.scale(f2, f3, f2);
	}

	@Override
	protected float getOverlayProgress(LivingEntity livingEntityIn, float partialTicks) {
		Creeper creeper = (Creeper) livingEntityIn;
		float f = creeper.getSwelling(partialTicks);
		return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
	}
}
