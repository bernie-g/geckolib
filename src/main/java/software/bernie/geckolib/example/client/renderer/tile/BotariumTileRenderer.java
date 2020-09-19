package software.bernie.geckolib.example.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import software.bernie.geckolib.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib.example.client.renderer.model.tile.BotariumModel;
import software.bernie.geckolib.geo.render.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity>
{
	public BotariumTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new BotariumModel());
	}
}
