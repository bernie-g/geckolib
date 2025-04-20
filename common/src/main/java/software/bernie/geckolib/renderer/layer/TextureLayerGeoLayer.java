package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

import java.util.function.Function;

/**
 * Built-in GeoLayer for quickly performing another render pass for the same model after the main render pass has completed.
 * <p>
 * This should only be used if the additional render pass isn't specific to any bones, as this re-renders the entire model.
 * If you are using this to use custom textures/rendertypes on specific bones, use {@link CustomBoneTextureGeoLayer} instead.
 */
public class TextureLayerGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    protected final ResourceLocation texture;
    protected final Function<ResourceLocation, RenderType> renderType;

    TextureLayerGeoLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, MissingTextureAtlasSprite.getLocation(), null);
    }

    public TextureLayerGeoLayer(GeoRenderer<T, O, R> renderer, ResourceLocation texture) {
        this(renderer, texture, null);
    }

    public TextureLayerGeoLayer(GeoRenderer<T, O, R> renderer, ResourceLocation texture, Function<ResourceLocation, RenderType> renderType) {
        super(renderer);

        this.texture = texture;
        this.renderType = renderType;
    }

    /**
     * Get the texture resource path for the given {@link GeoRenderState}
     */
    @Override
    protected ResourceLocation getTextureResource(R renderState) {
        return this.texture;
    }

    /**
     * Get the render type for the render pass
     */
    protected RenderType getRenderType(R renderState) {
        final ResourceLocation texture = getTextureResource(renderState);

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
    public void render(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                       int packedLight, int packedOverlay, int renderColor) {
        if (buffer == null)
            return;

        renderType = getRenderType(renderState);

        if (renderType != null)
            this.renderer.reRender(renderState, poseStack, bakedModel, bufferSource, renderType, bufferSource.getBuffer(renderType), packedLight, packedOverlay, renderColor);
    }
}
