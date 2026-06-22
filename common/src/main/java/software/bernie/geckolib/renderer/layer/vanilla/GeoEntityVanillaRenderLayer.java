package software.bernie.geckolib.renderer.layer.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link GeoEntity} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a GeoEntity on a vanilla entity
 */
public abstract class GeoEntityVanillaRenderLayer<A extends Entity & GeoEntity, T extends Entity, M extends EntityModel<T>, R extends GeoEntityRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance to render a {@link GeoEntity} on a vanilla entity model
	 *
	 * @param renderer        The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new GeoEntity instance for rendering
	 */
	public GeoEntityVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
	
	/**
	 * Get the {@link GeoRenderer} instance for this {@link GeoEntity}
	 */
	@Override
	protected @Nullable R getRenderer(A animatable) {
		final EntityRenderDispatcher entityRenderers = Minecraft.getInstance().getEntityRenderDispatcher();
		
		//noinspection unchecked
		return entityRenderers.getRenderer(animatable) instanceof GeoEntityRenderer<? super A> geoRenderer ? (R)geoRenderer : null;
	}
}
