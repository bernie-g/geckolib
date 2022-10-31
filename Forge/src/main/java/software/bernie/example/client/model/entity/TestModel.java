package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.TestEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TestModel extends AnimatedGeoModel<TestEntity> {
	@Override
	public ResourceLocation getAnimationResource(TestEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/parasite.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TestEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/parasite.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TestEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/entity/cow.png");
	}
}