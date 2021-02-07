package software.bernie.example.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.client.model.tile.BotariumModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity> {
	public BotariumTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn, new BotariumModel());
	}

	@Override
	public RenderType getRenderType(BotariumTileEntity animatable, float partialTicks, MatrixStack stack,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.getEntityTranslucent(getTextureLocation(animatable));
	}
}
