package software.bernie.example.client.model.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.renderer.entity.FakeGlassRenderer;
import software.bernie.example.entity.FakeGlassEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedEntityGeoModel;
import software.bernie.geckolib3.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link FakeGlassEntity}
 * @see FakeGlassRenderer
 */
public class FakeGlassModel extends DefaultedEntityGeoModel<FakeGlassEntity> {
	private static final ResourceLocation REDSTONE_BLOCK_TEXTURE =
			new ResourceLocation("minecraft", "textures/block/redstone_block.png");

	public FakeGlassModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "fake_glass"));
	}

	// We just want our texture to be the Redstone Block texture
	@Override
	public ResourceLocation getTextureResource(FakeGlassEntity animatable) {
		return REDSTONE_BLOCK_TEXTURE;
	}

	// We want our entity to be translucent
	@Override
	public RenderType getRenderType(FakeGlassEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
