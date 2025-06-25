package anightdazingzoroark.example.client.renderer.item;

import anightdazingzoroark.example.client.model.item.BombModel;
import anightdazingzoroark.example.item.BombItem;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;

public class BombRenderer extends GeoItemRenderer<BombItem> {
    public BombRenderer() {
        super(new BombModel());
    }
}
