package software.bernie.geckolib.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
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

	public void render(T tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		matrixStackIn.push();
		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		this.renderCustom(tile, matrixStackIn, partialTicks);
		matrixStackIn.translate(0.0D, (double) -1.501F, 0.0D);
		matrixStackIn.translate(-0.5, 0, 0.5);

		float f8 = 0.0F;
		float f5 = 0.0F;

		this.entityModel.setLivingAnimations(tile, f5, f8, partialTicks);
		boolean isVisible = true;
		ResourceLocation blockTexture = getBlockTexture(tile);
		RenderType rendertype = RenderType.getEntityCutoutNoCull(blockTexture);
		if (rendertype != null)
		{
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
			int i = 1;
			int noOverlay = OverlayTexture.NO_OVERLAY;
			this.entityModel.render(matrixStackIn, ivertexbuilder, 15728640, 655360, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		matrixStackIn.pop();
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


	private static float getFacingAngle(Direction facingIn)
	{
		switch (facingIn)
		{
			case SOUTH:
				return 90.0F;
			case WEST:
				return 0.0F;
			case NORTH:
				return 270.0F;
			case EAST:
				return 180.0F;
			default:
				return 0.0F;
		}
	}
}
