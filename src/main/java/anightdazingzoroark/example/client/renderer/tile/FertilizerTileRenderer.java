package anightdazingzoroark.example.client.renderer.tile;

import anightdazingzoroark.example.block.tile.FertilizerTileEntity;
import anightdazingzoroark.example.client.model.tile.FertilizerModel;
import anightdazingzoroark.riftlib.renderers.geo.GeoBlockRenderer;

public class FertilizerTileRenderer extends GeoBlockRenderer<FertilizerTileEntity> {
	public FertilizerTileRenderer() {
		super(new FertilizerModel());
	}
}
