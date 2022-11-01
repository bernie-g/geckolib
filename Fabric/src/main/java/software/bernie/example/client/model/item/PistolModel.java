package software.bernie.example.client.model.item;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PistolModel extends AnimatedGeoModel<PistolItem> {
	@Override
	public Identifier getModelLocation(PistolItem object) {
		return EntityResources.PISTOL_MODEL;
	}

	@Override
	public Identifier getTextureLocation(PistolItem object) {
		return EntityResources.PISTOL_TEXTURE;
	}

	@Override
	public Identifier getAnimationFileLocation(PistolItem animatable) {
		return EntityResources.PISTOL_ANIMATIONS;
	}
}
