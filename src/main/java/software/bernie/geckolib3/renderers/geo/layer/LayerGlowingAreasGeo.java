package software.bernie.geckolib3.renderers.geo.layer;

import java.util.function.Function;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
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

	protected final Function<Identifier, RenderLayer> funcGetEmissiveRenderType;

	public LayerGlowingAreasGeo(GeoEntityRenderer<T> renderer, Function<T, Identifier> funcGetCurrentTexture,
			Function<T, Identifier> funcGetCurrentModel, Function<Identifier, RenderLayer> funcGetEmissiveRenderType) {
		super(renderer, funcGetCurrentTexture, funcGetCurrentModel);

		this.funcGetEmissiveRenderType = funcGetEmissiveRenderType;
	}

	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
			T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch) {
		this.reRenderCurrentModelInRenderer(entityLivingBaseIn, partialTicks, matrixStackIn, bufferIn, packedLightIn,
				this.funcGetEmissiveRenderType
						.apply(AutoGlowingTexture.get(this.funcGetCurrentTexture.apply(entityLivingBaseIn))));
	}

}
