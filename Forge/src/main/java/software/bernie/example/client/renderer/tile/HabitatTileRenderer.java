package software.bernie.example.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.block.tile.HabitatTileEntity;
import software.bernie.example.client.model.tile.HabitatModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class HabitatTileRenderer extends GeoBlockRenderer<HabitatTileEntity> {
	public HabitatTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn, new HabitatModel());
	}

	@Override
	public RenderType getRenderType(HabitatTileEntity animatable, float partialTicks, MatrixStack stack,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
