package software.bernie.geckolib.renderer.layer.builtin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Function;

/**
 * Built-in GeoLayer for quickly performing another render pass for the same model after the main render pass has completed.
 * <p>
 * This should only be used if the additional render pass isn't specific to any bones, as this re-renders the entire model.
 * If you are using this to use custom textures/rendertypes on specific bones, use {@link CustomBoneTextureGeoLayer} instead.
 *
 * @param <T> Animatable class type. Inherited from the renderer this layer is attached to
 * @param <O> Associated object class type, or {@link Void} if none. Inherited from the renderer this layer is attached to
 * @param <R> RenderState class type. Inherited from the renderer this layer is attached to
 */
public class TextureLayerGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    protected final Identifier texture;
    protected final Function<Identifier, RenderType> renderType;

    TextureLayerGeoLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, MissingTextureAtlasSprite.getLocation(), null);
    }

    public TextureLayerGeoLayer(GeoRenderer<T, O, R> renderer, Identifier texture) {
        this(renderer, texture, null);
    }

    public TextureLayerGeoLayer(GeoRenderer<T, O, R> renderer, Identifier texture, Function<Identifier, RenderType> renderType) {
        super(renderer);

        this.texture = texture;
        this.renderType = renderType;
    }

    /**
     * Get the texture resource path for the given {@link GeoRenderState}
     */
    @Override
    protected Identifier getTextureResource(R renderState) {
        return this.texture;
    }

    /**
     * Get the render type for the render pass
     */
    protected RenderType getRenderType(R renderState) {
        final Identifier texture = getTextureResource(renderState);

        if (this.renderType == null)
            return this.renderer.getRenderType(renderState, texture);

        return this.renderType.apply(texture);
    }

    /**
     * This is the method that is actually called by the render for your render layer to function
     * <p>
     * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags
     * <p>
     * <b><u>NOTE:</u></b> If the passed {@link VertexConsumer buffer} is null, then the animatable was not actually rendered (invisible, etc)
     * and you may need to factor this in to your design
     */
    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (!renderPassInfo.willRender())
            return;

        RenderType renderType = getRenderType(renderPassInfo.renderState());

        if (renderType != null)
            this.renderer.submitRenderTasks(renderPassInfo, renderTasks.order(1), renderType);
    }
}
