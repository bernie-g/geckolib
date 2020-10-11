package software.bernie.geckolib.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;

import java.awt.*;

public abstract class GeoBlockRenderer<T extends TileEntity & IAnimatable> extends TileEntityRenderer implements IGeoRenderer<T>
{
	static
	{
		AnimationController.addModelFetcher((Object object) ->
		{
			if (object instanceof TileEntity)
			{
				TileEntity tile = (TileEntity) object;
				TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(tile);
				if (renderer instanceof GeoBlockRenderer)
				{
					return ((GeoBlockRenderer<?>) renderer).getGeoModelProvider();
				}
			}
			return null;
		});
	}

	private final AnimatedGeoModel<T> modelProvider;

	public GeoBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn, AnimatedGeoModel<T> modelProvider)
	{
		super(rendererDispatcherIn);
		this.modelProvider = modelProvider;
	}

	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		modelProvider.setLivingAnimations(tile);
		stack.push();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile), stack);

		Minecraft.getInstance().textureManager.bindTexture(modelProvider.getTextureLocation(tile));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(tile));
		Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn, modelProvider.getTextureLocation(tile));
		render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider()
	{
		return this.modelProvider;
	}


	private void rotateBlock(Direction facing, MatrixStack stack)
	{
		switch (facing)
		{
			case SOUTH:
				stack.rotate(Vector3f.YP.rotationDegrees(180));
				break;
			case WEST:
				stack.rotate(Vector3f.YP.rotationDegrees(90));
				break;
			case NORTH:
				stack.rotate(Vector3f.YP.rotationDegrees(0));
				break;
			case EAST:
				stack.rotate(Vector3f.YP.rotationDegrees(270));
				break;
			case UP:
				stack.rotate(Vector3f.XP.rotationDegrees(90));
				break;
			case DOWN:
				stack.rotate(Vector3f.XN.rotationDegrees(90));
				break;
		}
	}

	private Direction getFacing(T tile)
	{
		BlockState blockState = tile.getBlockState();
		if (blockState.has(HorizontalBlock.HORIZONTAL_FACING))
		{
			return blockState.get(HorizontalBlock.HORIZONTAL_FACING);
		}
		else if (blockState.has(DirectionalBlock.FACING))
		{
			return blockState.get(DirectionalBlock.FACING);
		}
		else
		{
			return Direction.NORTH;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T instance)
	{
		return this.modelProvider.getTextureLocation(instance);
	}
}
