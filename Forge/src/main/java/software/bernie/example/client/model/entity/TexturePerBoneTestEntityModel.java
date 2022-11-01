package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TexturePerBoneTestEntityModel<T extends LivingEntity & IAnimatable> extends AnimatedGeoModel<T>  {

	protected final ResourceLocation MODEL_RESLOC;
	protected final ResourceLocation TEXTURE_DEFAULT;
	protected final String ENTITY_REGISTRY_PATH_NAME;

	public TexturePerBoneTestEntityModel(ResourceLocation model, ResourceLocation textureDefault, String entityName) {
		super();
		this.MODEL_RESLOC = model;
		this.TEXTURE_DEFAULT = textureDefault;
		this.ENTITY_REGISTRY_PATH_NAME = entityName;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return EntityResources.TEXTUREPERBONE_ANIMATIONS;
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
