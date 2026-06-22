package software.bernie.geckolib.renderer.layer.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Function;

/**
 * Built-in implementation of {@link AttachedAnimatableRenderLayer} specifically for {@link GeoBlockEntity} rendering
 * <p>
 * This handles the vast majority of the boilerplate code for rendering a GeoBlockEntity on a vanilla entity
 */
public abstract class GeoBlockVanillaRenderLayer<A extends BlockEntity & GeoBlockEntity, T extends Entity, M extends EntityModel<T>, R extends GeoBlockRenderer<A>> extends AttachedAnimatableRenderLayer<A, T, M, R> {
	/**
	 * Create a new {@link RenderLayer} instance to render a {@link GeoBlockEntity} on a vanilla entity model
	 *
	 * @param renderer        The vanilla renderer instance that the layer is being added to
	 * @param instanceFactory A factory that creates a new GeoBlockEntity instance for rendering
	 */
	public GeoBlockVanillaRenderLayer(RenderLayerParent<T, M> renderer, Function<Level, A> instanceFactory) {
		super(renderer, instanceFactory);
	}
	/**
	 * Get the {@link GeoRenderer} instance for this {@link GeoAnimatable}
	 */
	/**
	 * Get the {@link GeoRenderer} instance for this {@link GeoBlockEntity}
	 */
	@Override
	protected @Nullable R getRenderer(A animatable) {
		final BlockEntityRenderDispatcher blockRenderers = Minecraft.getInstance().getBlockEntityRenderDispatcher();
		
		//noinspection unchecked
		return blockRenderers.getRenderer(animatable) instanceof GeoBlockRenderer<? super A> geoRenderer ? (R)geoRenderer : null;
	}
}
