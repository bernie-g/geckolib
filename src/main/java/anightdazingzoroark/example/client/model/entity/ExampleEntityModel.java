package anightdazingzoroark.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.example.entity.GeoExampleEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.processor.IBone;
import anightdazingzoroark.riftlib.model.AnimatedTickingGeoModel;
import anightdazingzoroark.riftlib.model.provider.data.EntityModelData;

public class ExampleEntityModel extends AnimatedTickingGeoModel<GeoExampleEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(GeoExampleEntity entity) {
		return new ResourceLocation(RiftLib.ModID, "animations/bat.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(GeoExampleEntity entity) {
		return new ResourceLocation(RiftLib.ModID, "geo/bat.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(GeoExampleEntity entity) {
		return new ResourceLocation(RiftLib.ModID, "textures/model/entity/bat.png");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setLivingAnimations(GeoExampleEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("head");
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
	}
}
