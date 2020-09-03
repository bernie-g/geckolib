package software.bernie.geckolib.example.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.block.AnimatedBlockRenderer;
import software.bernie.geckolib.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib.example.client.renderer.model.tile.BotariumModel;
import software.bernie.geckolib.render.CustomRenderTypes;

import static net.minecraft.client.renderer.RenderType.makeType;

public class BotariumTileRenderer extends AnimatedBlockRenderer<BotariumTileEntity, BotariumModel>
{
	public BotariumTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new BotariumModel());
	}

	@Override
	public ResourceLocation getBlockTexture(BotariumTileEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/botarium.png");
	}

	@Override
	protected RenderType getRenderType(BotariumTileEntity tile, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn, ResourceLocation textureLocation)
	{
		RenderSystem.enableDepthTest();
		return CustomRenderTypes.createTranslucentWaterRenderType(getBlockTexture(tile), true);

	}


}
