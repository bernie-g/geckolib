package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.renderer.entity.layer.CoolKidGlassesLayer;
import software.bernie.example.entity.CoolKidEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedEntityGeoModel;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoRenderer} implementation of an entity that uses a {@link software.bernie.geckolib3.renderer.layer.GeoRenderLayer render layer}
 * @see CoolKidGlassesLayer
 * @see CoolKidEntity
 */
public class CoolKidRenderer extends GeoEntityRenderer<CoolKidEntity> {
	public CoolKidRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(GeckoLib.MOD_ID, "cool_kid")));

		this.shadowRadius = 0.25f;

		// Add our render layer
		addRenderLayer(new CoolKidGlassesLayer(this));
    }
}
