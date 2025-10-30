package software.bernie.geckolib.renderer.internal;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.List;

/**
 * Callback class designed to hold positioning tasks for models prior to rendering.
 * <p>
 * This is used instead of hardcoding a callback to allow for extensible handling.
 */
public final class RenderModelPositioner<R extends GeoRenderState> {
    private final List<Callback<R>> callbacks;

    private RenderModelPositioner(List<Callback<R>> callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * Run the positioning tasks applied to this positioner.
     * <p>
     * Should only ever be called immediately prior to rendering, inside the render submit
     */
    public void run(R renderState, BakedGeoModel model) {
        for (Callback<R> callback : this.callbacks) {
            callback.run(renderState, model);
        }
    }

    /**
     * Add a {@link Callback} to the provided RenderModelPositioner, or create a new one if required
     * <p>
     * All new callbacks should be added through this method
     */
    public static <R extends GeoRenderState> RenderModelPositioner<R> add(@Nullable RenderModelPositioner<R> existing, Callback<R> callback) {
        if (existing == null)
            existing = new RenderModelPositioner<>(new ObjectArrayList<>(1));

        existing.callbacks.add(callback);

        return existing;
    }

    /**
     * Singular callback to run immediately prior to rendering a model
     */
    @FunctionalInterface
    public interface Callback<R extends GeoRenderState> {
        /**
         * Run the model positioner for this callback
         */
        void run(R renderState, BakedGeoModel model);
    }
}
