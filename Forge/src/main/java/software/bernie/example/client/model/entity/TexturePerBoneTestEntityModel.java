package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedEntityGeoModel;
import software.bernie.geckolib3.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link software.bernie.example.entity.TexturePerBoneTestEntity}
 * @see software.bernie.example.client.renderer.entity.TexturePerBoneTestEntityRenderer
 */
public class TexturePerBoneTestEntityModel extends DefaultedEntityGeoModel<TexturePerBoneTestEntity> {
	public TexturePerBoneTestEntityModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "textureperbonetestentity"));
	}
}
