package software.bernie.geckolib.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedBlockModel;

import javax.annotation.Nullable;

public abstract class AnimatedBlockRenderer<T extends TileEntity & ITileAnimatable, M extends AnimatedBlockModel> extends TileEntityRenderer
{
	protected M entityModel;

	public AnimatedBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn, M model)
	{
		super(rendererDispatcherIn);
		this.entityModel = model;
	}

	public abstract ResourceLocation getBlockTexture(T entity);

	public void renderCustom(T entity, MatrixStack matrixStackIn, float partialTicks)
	{
	}

	public M getEntityModel()
	{
		return this.entityModel;
	}

	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		this.render((T) tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		stack.push();
		stack.scale(-1.0F, -1.0F, 1.0F);
		this.renderCustom(tile, stack, partialTicks);
		stack.translate(0.0D, (double) -1.501F, 0.0D);
		stack.translate(-0.5, 0, 0.5);
		
		rotateBlock(getFacing(tile), stack);

		this.entityModel.setLivingAnimations(tile, partialTicks);
		boolean isVisible = true;
		ResourceLocation blockTexture = getBlockTexture(tile);
		RenderType rendertype = RenderType.getEntityCutoutNoCull(blockTexture);
		if (rendertype != null)
		{
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
			int i = 1;
			int noOverlay = OverlayTexture.NO_OVERLAY;
			this.entityModel.render(stack, ivertexbuilder, packedLightIn, 655360, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		stack.pop();
	}

	private void rotateBlock(Direction facing, MatrixStack stack)
	{
		switch (facing)
		{
			case SOUTH:
				stack.rotate(Vector3f.YP.rotationDegrees(180));
				break;
			case WEST:
				stack.rotate(Vector3f.YP.rotationDegrees(270));
				break;
			case NORTH:
				stack.rotate(Vector3f.YP.rotationDegrees(0));
				break;
			case EAST:
				stack.rotate(Vector3f.YP.rotationDegrees(90));
				break;
			case UP:
				stack.rotate(Vector3f.XP.rotationDegrees(90));
				break;
			case DOWN:
				stack.rotate(Vector3f.XN.rotationDegrees(90));
				break;
		}
	}

	@Nullable
	protected RenderType getEntityRenderType(T entity, boolean p_230042_2_, boolean isTranslucent)
	{
		ResourceLocation resourcelocation = this.getBlockTexture(entity);
		if (isTranslucent)
		{
			return RenderType.getEntityTranslucent(resourcelocation);
		}
		else if (p_230042_2_)
		{
			return this.entityModel.getRenderType(resourcelocation);
		}
		else
		{
			return RenderType.getOutline(resourcelocation);
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
}
