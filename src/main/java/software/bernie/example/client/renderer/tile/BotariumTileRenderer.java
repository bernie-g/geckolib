package software.bernie.example.client.renderer.tile;

import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.client.model.tile.BotariumModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity> {
	public BotariumTileRenderer() {
		super(new BotariumModel());
	}
}
