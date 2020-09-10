package software.bernie.geckolib.geo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;
import software.bernie.geckolib.model.IGeoModelProvider;

import java.awt.*;

public abstract class GeoBlockRenderer<T extends TileEntity & IAnimatable> extends TileEntityRenderer implements IGeoRenderer<T>
{
	private final AnimatedGeoModel<T> modelProvider;

	public GeoBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn, AnimatedGeoModel<T> modelProvider)
	{
		super(rendererDispatcherIn);
		this.modelProvider = modelProvider;
		this.modelProvider.crashWhenCantFindBone = false;
	}

	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T entityIn, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		modelProvider.setLivingAnimations(entityIn);
		stack.push();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0, 0.5);
		Minecraft.getInstance().textureManager.bindTexture(getTexture(entityIn));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entityIn));
		Color renderColor = getRenderColor(entityIn, partialTicks, stack, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(entityIn, partialTicks, stack, bufferIn, packedLightIn, getTexture(entityIn));
		render(model, entityIn, partialTicks, stack, bufferIn.getBuffer(renderType), packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
	}

	@Override
	public IGeoModelProvider getGeoModelProvider()
	{
		return this.modelProvider;
	}
}
