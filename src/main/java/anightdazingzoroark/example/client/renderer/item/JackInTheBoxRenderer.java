package anightdazingzoroark.example.client.renderer.item;

import anightdazingzoroark.example.client.model.item.JackInTheBoxModel;
import anightdazingzoroark.example.item.JackInTheBoxItem;
import anightdazingzoroark.riftlib.renderers.geo.GeoItemRenderer;

public class JackInTheBoxRenderer extends GeoItemRenderer<JackInTheBoxItem> {
	public JackInTheBoxRenderer() {
		super(new JackInTheBoxModel());
	}
}
