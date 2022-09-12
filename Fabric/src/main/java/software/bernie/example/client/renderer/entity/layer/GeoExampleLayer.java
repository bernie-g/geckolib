package software.bernie.example.client.renderer.entity.layer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@SuppressWarnings("rawtypes")
public class GeoExampleLayer extends GeoLayerRenderer {
    // A resource location for the texture of the layer. This will be applied onto pre-existing cubes on the model
    private static final Identifier LAYER = new Identifier(GeckoLib.ModID, "textures/entity/le_glasses.png");
    // A resource location for the model of the entity. This model is put on top of the normal one, which is then given the texture
    private static final Identifier MODEL = new Identifier(GeckoLib.ModID, "geo/le.geo.json");

    @SuppressWarnings("unchecked")
	public GeoExampleLayer(IGeoRenderer<?> entityRendererIn) {
        super(entityRendererIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, Entity entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer cameo =  RenderLayer.getArmorCutoutNoCull(LAYER);
        matrixStackIn.push();
        //Move or scale the model as you see fit
        matrixStackIn.scale(1.0f, 1.0f, 1.0f);
        matrixStackIn.translate(0.0d, 0.0d, 0.0d);
        this.getRenderer().render(this.getEntityModel().getModel(MODEL), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        matrixStackIn.pop();
    }
}