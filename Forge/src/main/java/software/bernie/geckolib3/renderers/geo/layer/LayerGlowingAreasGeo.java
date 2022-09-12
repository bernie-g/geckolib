package software.bernie.geckolib3.renderers.geo.layer;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
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
public class LayerGlowingAreasGeo<T extends MobEntity & IAnimatable> extends AbstractLayerGeo<T> {
	
	protected final Function<ResourceLocation, RenderType> funcGetEmissiveRenderType;

	public LayerGlowingAreasGeo(GeoEntityRenderer<T> renderer, Function<T, ResourceLocation> funcGetCurrentTexture, Function<T, ResourceLocation> funcGetCurrentModel, Function<ResourceLocation, RenderType> funcGetEmissiveRenderType) {
		super(renderer, funcGetCurrentTexture, funcGetCurrentModel);
		
		this.funcGetEmissiveRenderType = funcGetEmissiveRenderType;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		this.reRenderCurrentModelInRenderer(entityLivingBaseIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, this.funcGetEmissiveRenderType.apply(AutoGlowingTexture.get(this.funcGetCurrentTexture.apply(entityLivingBaseIn))));
	}

}
