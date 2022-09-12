package software.bernie.example.client.renderer.item;

import software.bernie.example.client.model.item.JackInTheBoxModel;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3q.renderers.geo.GeoItemRenderer;

public class JackInTheBoxRenderer extends GeoItemRenderer<JackInTheBoxItem> {
	public JackInTheBoxRenderer() {
		super(new JackInTheBoxModel());
	}
}
