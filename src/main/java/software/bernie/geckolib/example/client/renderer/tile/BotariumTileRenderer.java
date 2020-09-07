package software.bernie.geckolib.example.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib.example.client.renderer.model.tile.BotariumModel;
import software.bernie.geckolib.geo.render.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity>
{
	public BotariumTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn, new BotariumModel());
	}

	@Override
	public ResourceLocation getTexture(BotariumTileEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/botarium.png");
	}

}
