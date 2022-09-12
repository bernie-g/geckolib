package software.bernie.example.client.model.item;

import net.minecraft.util.Identifier;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class JackInTheBoxModel extends AnimatedGeoModel<JackInTheBoxItem> {
	@Override
	public Identifier getModelResource(JackInTheBoxItem object) {
		return new Identifier(GeckoLib.ModID, "geo/jack.geo.json");
	}

	@Override
	public Identifier getTextureResource(JackInTheBoxItem object) {
		return new Identifier(GeckoLib.ModID, "textures/item/jack.png");
	}

	@Override
	public Identifier getAnimationResource(JackInTheBoxItem animatable) {
		return new Identifier(GeckoLib.ModID, "animations/jackinthebox.animation.json");
	}
}
