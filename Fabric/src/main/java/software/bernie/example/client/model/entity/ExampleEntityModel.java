package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ExampleEntityModel extends AnimatedTickingGeoModel<GeoExampleEntity> {
	@Override
	public Identifier getAnimationFileLocation(GeoExampleEntity entity) {
		return EntityResources.BAT_ANIMATIONS;
	}

	@Override
	public Identifier getModelLocation(GeoExampleEntity entity) {
		return EntityResources.BAT_MODEL;
	}

	@Override
	public Identifier getTextureLocation(GeoExampleEntity entity) {
		return EntityResources.BAT_TEXTURE;
	}

	@Override
	public void setCustomAnimations(GeoExampleEntity animatable, int instanceId, AnimationEvent animationEvent) {
		super.setCustomAnimations(animatable, instanceId, animationEvent);
		IBone head = this.getAnimationProcessor().getBone("head");

		EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
		if (head != null) {
			head.setRotationX(extraData.headPitch * MathHelper.RADIANS_PER_DEGREE);
			head.setRotationY(extraData.netHeadYaw * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}
