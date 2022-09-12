package software.bernie.geckolib3.renderers.geo.layer;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.texture.AutoGlowingTexture;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Render layer for emissive textures
 * 
 * Originally developed for chocolate quest repoured
 */
public class LayerGlowingAreasGeo<T extends Mob & IAnimatable> extends AbstractLayerGeo<T> {

	protected final Function<ResourceLocation, RenderType> funcGetEmissiveRenderType;

	public LayerGlowingAreasGeo(GeoEntityRenderer<T> renderer, Function<T, ResourceLocation> funcGetCurrentTexture,
			Function<T, ResourceLocation> funcGetCurrentModel,
			Function<ResourceLocation, RenderType> funcGetEmissiveRenderType) {
		super(renderer, funcGetCurrentTexture, funcGetCurrentModel);

		this.funcGetEmissiveRenderType = funcGetEmissiveRenderType;
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.reRenderCurrentModelInRenderer(entityLivingBaseIn, partialTicks, matrixStackIn, bufferIn, packedLightIn,
				this.funcGetEmissiveRenderType
						.apply(AutoGlowingTexture.get(this.funcGetCurrentTexture.apply(entityLivingBaseIn))));
	}

}
