package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.client.model.entity.FakeGlassModel;
import software.bernie.example.entity.FakeGlassEntity;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.renderer.ExtendedGeoEntityRenderer;

/**
 * Example {@link ExtendedGeoEntityRenderer} implementation that makes use of the per-bone texture overrides feature
 * @see FakeGlassEntity
 * @see FakeGlassModel
 */
public class FakeGlassRenderer extends ExtendedGeoEntityRenderer<FakeGlassEntity> {
	private static final ResourceLocation WHITE_STAINED_GLASS_TEXTURE =
			new ResourceLocation("textures/block/white_stained_glass.png");

	public FakeGlassRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new FakeGlassModel());
	}

	// If the bone named "outer_glass" is being rendered, render it using white stained-glass texture
	@Nullable
	@Override
	protected ResourceLocation getTextureOverrideForBone(GeoBone bone, FakeGlassEntity animatable, float partialTick) {
		return bone.getName().equals("outer_glass") ? WHITE_STAINED_GLASS_TEXTURE : null;
	}
}