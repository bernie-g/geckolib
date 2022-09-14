package software.bernie.geckolib3.renderers.geo.layer;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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

	protected final Function<T, ResourceLocation> funcGetCurrentTexture;
	protected final Function<T, ResourceLocation> funcGetCurrentModel;

	protected GeoEntityRenderer<T> geoRendererInstance;

	public AbstractLayerGeo(GeoEntityRenderer<T> renderer, Function<T, ResourceLocation> funcGetCurrentTexture,
			Function<T, ResourceLocation> funcGetCurrentModel) {
		super(renderer);
		this.geoRendererInstance = renderer;
		this.funcGetCurrentTexture = funcGetCurrentTexture;
		this.funcGetCurrentModel = funcGetCurrentModel;
	}

	/*
	 * Utility method to force the renderer to re-render the model a second time
	 */
	protected void reRenderCurrentModelInRenderer(T entity, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn, RenderType cameo) {
		matrixStackIn.pushPose();

		this.getRenderer().render(this.getEntityModel().getModel(this.funcGetCurrentModel.apply(entity)), entity,
				partialTicks, cameo, matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn,
				OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

		matrixStackIn.popPose();
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
