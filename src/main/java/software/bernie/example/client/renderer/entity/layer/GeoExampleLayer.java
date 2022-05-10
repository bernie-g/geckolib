package software.bernie.example.client.renderer.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@SuppressWarnings("rawtypes")
public class GeoExampleLayer extends GeoLayerRenderer {

	// A resource location for the texture of the layer. This will be applied onto
	// pre-existing cubes on the model
	private static final ResourceLocation LAYER = new ResourceLocation(GeckoLib.ModID,
			"textures/model/entity/le_glasses.png");
	// A resource location for the model of the entity. This model is put on top of
	// the normal one, which is then given the texture
	private static final ResourceLocation MODEL = new ResourceLocation(GeckoLib.ModID, "geo/le.geo.json");

	@SuppressWarnings({ "unchecked"})
	public GeoExampleLayer(IGeoRenderer entityRendererIn) {
		super(entityRendererIn);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void render(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, Color renderColor) {
		Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(LAYER);
		entityRenderer.render(entityRenderer.getGeoModelProvider().getModel(MODEL), entitylivingbaseIn, partialTicks, (float) renderColor.getRed() / 255f, (float) renderColor.getBlue() / 255f,
				(float) renderColor.getGreen() / 255f, (float) renderColor.getAlpha() / 255);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
