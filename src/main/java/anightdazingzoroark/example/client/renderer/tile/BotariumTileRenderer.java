package anightdazingzoroark.example.client.renderer.tile;

import anightdazingzoroark.example.block.tile.BotariumTileEntity;
import anightdazingzoroark.example.client.model.tile.BotariumModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoBlockRenderer;

public class BotariumTileRenderer extends GeoBlockRenderer<BotariumTileEntity> {
	public BotariumTileRenderer() {
		super(new BotariumModel());
	}
}
