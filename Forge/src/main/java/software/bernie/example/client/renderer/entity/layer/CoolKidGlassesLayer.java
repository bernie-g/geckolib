package software.bernie.example.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.CoolKidEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.renderer.GeoRenderer;
import software.bernie.geckolib3.renderer.layer.GeoRenderLayer;

/**
 * Example implementation of a {@link GeoRenderLayer}.<br>
 * Renders a pair of glasses over the {@link CoolKidEntity} example entity
 * @see software.bernie.example.client.renderer.entity.CoolKidRenderer
 */
public class CoolKidGlassesLayer extends GeoRenderLayer<CoolKidEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GeckoLib.MOD_ID, "textures/entity/cool_kid_glasses.png");

	public CoolKidGlassesLayer(GeoRenderer<CoolKidEntity> entityRenderer) {
        super(entityRenderer);
    }

    // Apply the glasses texture layer to the existing geo model, and render it over the top of the existing model
    @Override
    public void render(PoseStack poseStack, CoolKidEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(TEXTURE);

        renderModel(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
                bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                1, 1, 1, 1);
    }
}