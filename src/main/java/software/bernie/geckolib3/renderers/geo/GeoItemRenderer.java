package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;

public abstract class GeoItemRenderer<T extends Item & IAnimatable> extends ItemStackTileEntityRenderer implements IGeoRenderer<T>
{
	// Register a model fetcher for this renderer
	static
	{
		AnimationController.addModelFetcher((IAnimatable object) ->
		{
			if (object instanceof Item)
			{
				Item item = (Item) object;
				ItemStackTileEntityRenderer renderer = item.getItemStackTileEntityRenderer();
				if (renderer instanceof GeoItemRenderer)
				{
					return ((GeoItemRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});
	}

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;
	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider)
	{
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model)
	{
		this.modelProvider = model;
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider()
	{
		return modelProvider;
	}

	@Override
	public void render(ItemStack itemStack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
	}

	public void render(T animatable, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ItemStack itemStack)
	{
		this.currentItemStack = itemStack;
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
		AnimationEvent itemEvent = new AnimationEvent(animatable, 0, 0, Minecraft.getInstance().getRenderPartialTicks(), false, Collections.singletonList(itemStack));
		modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		stack.push();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		Minecraft.getInstance().textureManager.bindTexture(modelProvider.getTextureLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn, modelProvider.getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
	}

	protected RenderType getRenderType(T tile, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ResourceLocation textureLocation)
	{
		return RenderType.getEntityCutoutNoCull(textureLocation);
	}

	protected Color getRenderColor(T tile, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		return new Color(255, 255, 255, 255);
	}

	@Override
	public ResourceLocation getTextureLocation(T instance)
	{
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Integer getUniqueID(T animatable)
	{
		return Objects.hash(currentItemStack.getItem(), currentItemStack.getCount(), currentItemStack.hasTag() ? currentItemStack.getTag().toString() : 1);
	}
}
