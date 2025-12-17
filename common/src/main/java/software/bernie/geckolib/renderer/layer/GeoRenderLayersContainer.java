package software.bernie.geckolib.renderer.layer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

import java.util.List;

/**
 * Base interface for a container for {@link GeoRenderLayer GeoRenderLayers}
 * <p>
 * Each renderer should contain an instance of this, for holding its layers and handling events
 *
 * @param <T> Animatable class type. Inherited from the renderer this container belongs to
 * @param <O> Associated object class type, or {@link Void} if none. Inherited from the renderer this container belongs to
 * @param <R> RenderState class type. Inherited from the renderer this container belongs to
 */
@ApiStatus.Internal
public class GeoRenderLayersContainer<T extends GeoAnimatable, O, R extends GeoRenderState> {
	private final GeoRenderer<T, O, R> renderer;
	private final List<GeoRenderLayer<T, O, R>> layers = new ObjectArrayList<>();
	private boolean compiledLayers = false;

	public GeoRenderLayersContainer(GeoRenderer<T, O, R> renderer) {
		this.renderer = renderer;
	}

	/**
	 * Get the {@link GeoRenderLayer} list for usage
	 */
	public List<GeoRenderLayer<T, O, R>> getRenderLayers() {
		if (!this.compiledLayers)
			fireCompileRenderLayersEvent();

		return this.layers;
	}

	/**
	 * Add a new render layer to the container
	 */
	public void addLayer(GeoRenderLayer<T, O, R> layer) {
		this.layers.add(layer);
	}

	/**
	 * Create and fire the relevant {@code CompileRenderLayers} event hook for the owning renderer
	 */
	public void fireCompileRenderLayersEvent() {
		this.compiledLayers = true;

		this.renderer.fireCompileRenderLayersEvent();
	}
}
