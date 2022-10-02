package software.bernie.example.client.model.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TexturePerBoneTestEntityModel<T extends LivingEntity & IAnimatable> extends AnimatedGeoModel<T>  {

	protected static final Identifier ANIMATION_RESLOC = new Identifier(GeckoLib.ModID, "animations/textureperbonetestentity.animation.json");
	protected final Identifier MODEL_RESLOC;
	protected final Identifier TEXTURE_DEFAULT;
	protected final String ENTITY_REGISTRY_PATH_NAME;

	public TexturePerBoneTestEntityModel(Identifier model, Identifier textureDefault, String entityName) {
		super();
		this.MODEL_RESLOC = model;
		this.TEXTURE_DEFAULT = textureDefault;
		this.ENTITY_REGISTRY_PATH_NAME = entityName;
	}

	@Override
	public Identifier getAnimationFileLocation(T animatable) {
		return ANIMATION_RESLOC;
	}

	@Override
	public Identifier getModelLocation(T object) {
		return MODEL_RESLOC;
	}

	@Override
	public Identifier getTextureLocation(T object) {
		return TEXTURE_DEFAULT;
	}

}
