package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3q.model.provider.data.EntityModelData;

public class ExampleEntityModel extends AnimatedTickingGeoModel<GeoExampleEntity> {
	@Override
	public ResourceLocation getAnimationResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/bat.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/bat.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/bat.png");
	}

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
