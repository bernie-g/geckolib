package software.bernie.geckolib3.renderers.geo.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import java.util.function.Function;

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

	public AbstractLayerGeo(GeoEntityRenderer<T> renderer, Function<T, ResourceLocation> currentTextureFunction,
			Function<T, ResourceLocation> currentModelFunction) {
		super(renderer);

		this.geoRendererInstance = renderer;
		this.funcGetCurrentTexture = currentTextureFunction;
		this.funcGetCurrentModel = currentModelFunction;
	}

	protected void reRenderCurrentModelInRenderer(T animatable, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, RenderType renderType) {
		poseStack.pushPose();
		getRenderer().render(getEntityModel().getModel(this.funcGetCurrentModel.apply(animatable)), animatable,
				partialTick, renderType, poseStack, bufferSource, bufferSource.getBuffer(renderType), packedLight,
				OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		poseStack.popPose();
	}

}
