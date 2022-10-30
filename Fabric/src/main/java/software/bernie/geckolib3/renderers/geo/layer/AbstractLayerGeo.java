package software.bernie.geckolib3.renderers.geo.layer;

import java.util.function.Function;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

/*
 * Copyright: DerToaster98 - 13.06.2022
 * 
 * Generic base class for more advanced layer renderers
 * 
 * Originally developed for chocolate quest repoured
 */
public abstract class AbstractLayerGeo<T extends LivingEntity & IAnimatable> extends GeoLayerRenderer<T> {
	protected final Function<T, Identifier> funcGetCurrentTexture;
	protected final Function<T, Identifier> funcGetCurrentModel;

	protected GeoEntityRenderer<T> geoRendererInstance;

	public AbstractLayerGeo(GeoEntityRenderer<T> renderer, Function<T, Identifier> currentTextureFunction,
			Function<T, Identifier> currentModelFunction) {
		super(renderer);

		this.geoRendererInstance = renderer;
		this.funcGetCurrentTexture = currentTextureFunction;
		this.funcGetCurrentModel = currentModelFunction;
	}

	protected void reRenderCurrentModelInRenderer(T animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, int packedLight, RenderLayer renderType) {
		poseStack.push();
		getRenderer().render(getEntityModel().getModel(this.funcGetCurrentModel.apply(animatable)), animatable,
				partialTick, renderType, poseStack, bufferSource, bufferSource.getBuffer(renderType), packedLight,
				OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		poseStack.pop();
	}

}
