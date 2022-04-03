package software.bernie.example.client.model.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ExampleExtendedRendererEntityModel<T extends LivingEntity & IAnimatable> extends AnimatedGeoModel<T>  {

	protected static final ResourceLocation ANIMATION_RESLOC = new ResourceLocation(GeckoLib.ModID, "animations/extendedrendererentity.animation.json");
	protected final ResourceLocation MODEL_RESLOC;
	protected final ResourceLocation TEXTURE_DEFAULT;
	protected final String ENTITY_REGISTRY_PATH_NAME;

	public ExampleExtendedRendererEntityModel(ResourceLocation model, ResourceLocation textureDefault, String entityName) {
		super();
		this.MODEL_RESLOC = model;
		this.TEXTURE_DEFAULT = textureDefault;
		this.ENTITY_REGISTRY_PATH_NAME = entityName;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return ANIMATION_RESLOC;
	}

	@Override
	public ResourceLocation getModelLocation(T object) {
		return MODEL_RESLOC;
	}

	@Override
	public ResourceLocation getTextureLocation(T object) {
		return TEXTURE_DEFAULT;
	}

}
