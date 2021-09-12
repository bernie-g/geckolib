package software.bernie.example.client.model.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ExampleEntityModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getAnimationFileLocation(Object entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/bat.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(Object entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/bat.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/bat.png");
	}

	@Override
	public void setLivingAnimations(IAnimatable entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("head");

		LivingEntity entityIn = (LivingEntity) entity;
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
	}
}
