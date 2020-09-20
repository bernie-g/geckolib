package software.bernie.example.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.model.tile.FertilizerModel;
import software.bernie.geckolib.renderers.geo.GeoBlockRenderer;

public class FertilizerTileRenderer extends GeoBlockRenderer<FertilizerTileEntity>
{
	public FertilizerTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new FertilizerModel());
	}
}
