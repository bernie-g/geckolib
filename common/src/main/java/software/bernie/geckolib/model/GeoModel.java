package software.bernie.geckolib.model;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Base class for all code-based model objects
 * <p>
 * All models registered to a {@link GeoRenderer} should be an instance of this or one of its subclasses
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Models">GeckoLib Wiki - Models</a>
 */
public abstract class GeoModel<T extends GeoAnimatable> {
	/**
	 * Returns the resource ID for the {@link BakedGeoModel} (model JSON file) to render based on the provided {@link GeoRenderState}
	 */
	public abstract Identifier getModelResource(GeoRenderState renderState);

	/**
	 * Returns the resource path for the texture file to render based on the provided {@link GeoRenderState}
	 */
	public abstract Identifier getTextureResource(GeoRenderState renderState);

	/**
	 * Returns the resource ID for the {@link BakedAnimations} (animation JSON file) to use for animations based on the provided animatable
	 */
	public abstract Identifier getAnimationResource(T animatable);

	/**
	 * Returns the resource path for the {@link BakedAnimations} (animation JSON file) fallback locations in the event
	 * your animation isn't present in the {@link #getAnimationResource(GeoAnimatable) primary resource}.
	 * <p>
	 * Should <b><u>NOT</u></b> be used as the primary animation resource path, and in general shouldn't be used
	 * at all unless you know what you are doing
	 */
	public Identifier[] getAnimationResourceFallbacks(T animatable) {
		return new Identifier[0];
	}

    /**
     * Add any additional {@link DataTicket}s you use in this model's getters here for use
     *
     * @param animatable The animatable being rendered
     * @param relatedObject The associated data for the renderer calling this method (E.G. {@link GeoItemRenderer.RenderData})
     * @param renderState The render state being compiled for this render pass
     */
    public void addAdditionalStateData(T animatable, Object relatedObject, GeoRenderState renderState) {}

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">

	/**
	 * Get the baked geo model object used for rendering from the given resource path
	 */
    @ApiStatus.Internal
	public BakedGeoModel getBakedModel(Identifier location) {
        return GeckoLibResources.getBakedModels().getModel(location);
	}

	/**
	 * Gets the baked {@link Animation} for the given animation {@code name}, if it exists
	 *
	 * @param animatable The {@link GeoAnimatable} for the upcoming render pass
	 * @param name The name of the animation to retrieve
	 * @return The Animation instance for the provided {@code name}, or null if none match
	 */
	@ApiStatus.Internal
    @Nullable
	public Animation getBakedAnimation(T animatable, String name) throws RuntimeException {
        final Identifier animationFile = getAnimationResource(animatable);
        final Identifier[] fallbackFiles = getAnimationResourceFallbacks(animatable);

        return GeckoLibResources.getBakedAnimations().getAnimation(animationFile, fallbackFiles, name);
	}
    //</editor-fold>
}
