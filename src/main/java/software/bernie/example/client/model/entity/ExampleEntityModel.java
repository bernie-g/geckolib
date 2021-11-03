package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ExampleEntityModel extends AnimatedTickingGeoModel<GeoExampleEntity> {
	@Override
	public Identifier getAnimationFileLocation(GeoExampleEntity entity) {
		return new Identifier(GeckoLib.ModID, "animations/bat.animation.json");
	}

	@Override
	public Identifier getModelLocation(GeoExampleEntity entity) {
		return new Identifier(GeckoLib.ModID, "geo/bat.geo.json");
	}

	@Override
	public Identifier getTextureLocation(GeoExampleEntity entity) {
		return new Identifier(GeckoLib.ModID, "textures/model/entity/bat.png");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setLivingAnimations(GeoExampleEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("head");

		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		if (head != null) {
			head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
			head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
		}
	}
}
