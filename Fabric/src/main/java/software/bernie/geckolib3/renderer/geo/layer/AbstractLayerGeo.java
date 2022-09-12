package software.bernie.geckolib3.renderer.geo.layer;

import java.util.function.Function;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderer.geo.GeoLayerRenderer;

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

	public AbstractLayerGeo(GeoEntityRenderer<T> renderer, Function<T, Identifier> funcGetCurrentTexture,
			Function<T, Identifier> funcGetCurrentModel) {
		super(renderer);
		this.geoRendererInstance = renderer;
		this.funcGetCurrentTexture = funcGetCurrentTexture;
		this.funcGetCurrentModel = funcGetCurrentModel;
	}

	/*
	 * Utility method to force the renderer to re-render the model a second time
	 */
	protected void reRenderCurrentModelInRenderer(T entity, float partialTicks, MatrixStack matrixStackIn,
			VertexConsumerProvider bufferIn, int packedLightIn, RenderLayer cameo) {
		matrixStackIn.push();

		this.getRenderer().render(this.getEntityModel().getModel(this.funcGetCurrentModel.apply(entity)), entity,
				partialTicks, cameo, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn,
				OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);

		matrixStackIn.pop();
		// 1.12.2
		/*
		 * this.getRenderer().render(
		 * this.getEntityModel().getModel(this.funcGetCurrentModel.apply(entity)),
		 * entity, partialTicks, (float) renderColor.getRed() / 255f, (float)
		 * renderColor.getBlue() / 255f, (float) renderColor.getGreen() / 255f, (float)
		 * renderColor.getAlpha() / 255 );
		 */
	}

}
