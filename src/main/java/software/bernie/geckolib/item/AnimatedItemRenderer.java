package software.bernie.geckolib.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javafx.scene.paint.Color;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.SpecialAnimatedModel;
import software.bernie.geckolib.entity.IAnimatable;

public abstract class AnimatedItemRenderer<T extends Item & IAnimatable, M extends SpecialAnimatedModel> extends ItemStackTileEntityRenderer
{
	protected M entityModel;

	public void setModel(M model)
	{
		this.entityModel = model;
	}

	public abstract ResourceLocation getBlockTexture(T entity);

	public void renderCustom(T entity, MatrixStack matrixStackIn)
	{
	}

	public M getEntityModel()
	{
		return this.entityModel;
	}

	@Override
	public void render(ItemStack itemStack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
	}


	public void render(T tile, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ItemStack itemStack)
	{
		stack.push();
		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.translate(0.0D, (double) -1.501F, 0.0D);
		stack.translate(-0.5, 0, 0.5);

		this.renderCustom(tile, stack);

		this.entityModel.setLivingAnimations(tile);
		ResourceLocation blockTexture = getBlockTexture(tile);
		RenderType rendertype = getRenderType(tile, stack, bufferIn, packedLightIn, blockTexture);
		if (rendertype != null)
		{
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
			Color renderColor = getRenderColor(tile, stack, bufferIn, packedLightIn);
			this.entityModel.render(stack, ivertexbuilder, packedLightIn, 655360, (float) renderColor.getRed(), (float) renderColor.getGreen(), (float) renderColor.getBlue(), (float) renderColor.getOpacity());
		}
		stack.pop();
	}

	protected RenderType getRenderType(T tile, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ResourceLocation textureLocation)
	{
		return RenderType.getEntityCutoutNoCull(textureLocation);
	}

	protected Color getRenderColor(T tile, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		return new Color(1, 1, 1, 1);
	}
}
