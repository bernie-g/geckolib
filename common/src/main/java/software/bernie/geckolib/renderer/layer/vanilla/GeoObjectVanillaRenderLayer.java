package software.bernie.geckolib.renderer.layer.vanilla;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link GeoObjectRenderer} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a GeoObjectRenderer on a vanilla entity
 */
public abstract class GeoObjectVanillaRenderLayer<A extends GeoAnimatable, T extends Entity, M extends EntityModel<T>, R extends GeoObjectRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance
	 *
	 * @param renderer The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new GeoObjectRenderer instance for rendering
	 */
	public GeoObjectVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
}
