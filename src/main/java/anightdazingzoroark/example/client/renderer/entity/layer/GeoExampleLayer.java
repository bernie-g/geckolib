package anightdazingzoroark.example.client.renderer.entity.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.core.util.Color;
import anightdazingzoroark.riftlib.renderers.geo.GeoLayerRenderer;
import anightdazingzoroark.riftlib.renderers.geo.IGeoRenderer;

@SuppressWarnings("rawtypes")
public class GeoExampleLayer extends GeoLayerRenderer {

	// A resource location for the texture of the layer. This will be applied onto
	// pre-existing cubes on the model
	private static final ResourceLocation LAYER = new ResourceLocation(RiftLib.ModID,
			"textures/model/entity/le_glasses.png");
	// A resource location for the model of the entity. This model is put on top of
	// the normal one, which is then given the texture
	private static final ResourceLocation MODEL = new ResourceLocation(RiftLib.ModID, "geo/le.geo.json");

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
