package software.bernie.geckolib.renderer.layer.vanilla;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link GeoItem} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a GeoItem on a vanilla entity
 */
public abstract class GeoItemVanillaRenderLayer<A extends Item & GeoItem, T extends Entity, M extends EntityModel<T>, R extends GeoItemRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance
	 *
	 * @param renderer The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new GeoItem instance for rendering
	 */
	public GeoItemVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
	
	/**
	 * Get the {@link GeoRenderer} instance for this {@link GeoItem}
	 */
	@Override
	protected @Nullable R getRenderer(A animatable) {
		//noinspection unchecked,rawtypes
		return GeoRenderProvider.of(animatable).getGeoItemRenderer() instanceof GeoItemRenderer geoRenderer ? (R)geoRenderer : null;
	}
}
