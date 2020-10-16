package software.bernie.example.client.renderer.tile;

import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.client.renderer.model.tile.BotariumModel;
import software.bernie.geckolib.renderer.geo.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity>
{
	public BotariumTileRenderer(BlockEntityRenderDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new BotariumModel());
	}
}
