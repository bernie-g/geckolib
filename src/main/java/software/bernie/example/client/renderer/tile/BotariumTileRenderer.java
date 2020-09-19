package software.bernie.example.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.client.renderer.model.tile.BotariumModel;
import software.bernie.geckolib.renderers.geo.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity>
{
	public BotariumTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new BotariumModel());
	}
}
