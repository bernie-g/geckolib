package software.bernie.example.client.model.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class ExampleExtendedRendererEntityModel<T extends LivingEntity & IAnimatable> extends AnimatedGeoModel<T> {

	protected static final Identifier ANIMATION_RESLOC = new Identifier(GeckoLib.ModID,
			"animations/extendedrendererentity.animation.json");
	protected final Identifier MODEL_RESLOC;
	protected final Identifier TEXTURE_DEFAULT;
	protected final String ENTITY_REGISTRY_PATH_NAME;

	public ExampleExtendedRendererEntityModel(Identifier model, Identifier textureDefault,
			String entityName) {
		super();
		this.MODEL_RESLOC = model;
		this.TEXTURE_DEFAULT = textureDefault;
		this.ENTITY_REGISTRY_PATH_NAME = entityName;
	}

	@Override
	public Identifier getAnimationResource(T animatable) {
		return ANIMATION_RESLOC;
	}

	@Override
	public Identifier getModelResource(T object) {
		return MODEL_RESLOC;
	}

	@Override
	public Identifier getTextureResource(T object) {
		return TEXTURE_DEFAULT;
	}

}
