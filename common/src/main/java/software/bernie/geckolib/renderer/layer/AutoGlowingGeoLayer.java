package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Built-in {@link GeoRenderLayer} for rendering an emissive layer over an existing GeoAnimatable.
 * <p>
 * By default, it uses a custom RenderType created by GeckoLib to make things easier, but it can be overridden as needed
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class AutoGlowingGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends TextureLayerGeoLayer<T, O, R> {
	private final Map<ResourceLocation, ResourceLocation> emissiveResourceCache = new Object2ObjectOpenHashMap<>();
	protected static final RenderPipeline RENDER_PIPELINE = RenderPipelines.register(createRenderPipeline());
	protected static final TriFunction<ResourceLocation, Boolean, Boolean, RenderType> RENDER_TYPE = memoizeRenderType(AutoGlowingGeoLayer::createRenderType);

	public AutoGlowingGeoLayer(GeoRenderer<T, O, R> renderer) {
		super(renderer);
	}

	/**
	 * Get the texture resource path for the given {@link GeoRenderState}.
	 */
	@Override
	protected ResourceLocation getTextureResource(R renderState) {
		return this.emissiveResourceCache.computeIfAbsent(this.renderer.getTextureLocation(renderState), RenderUtil::getEmissiveResource);
	}

	/**
	 * Override to return true if you want your emissive texture to respect lighting in the world.
	 * <p>
	 * This will lower its overall brightness slightly but allow it to shade properly.
	 */
	protected boolean shouldRespectWorldLighting() {
		return false;
	}

	/**
	 * Get the render type to use for this glowlayer renderer, or null if the layer should not render
	 * <p>
	 * Uses a custom RenderType similar to {@link RenderType#eyes(ResourceLocation)} by default
	 * <p>
	 * Automatically accounts for entity states like invisibility and glowing
	 */
	@Nullable
	@Override
	protected RenderType getRenderType(R renderState) {
		ResourceLocation texture = getTextureResource(renderState);
		boolean respectLighting = shouldRespectWorldLighting();

		if (!(renderState instanceof EntityRenderState entityRenderState))
			return RENDER_TYPE.apply(texture, false, respectLighting);

		boolean invisible = entityRenderState.isInvisible;

		if (invisible && !renderState.getOrDefaultGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, false))
			return RenderType.itemEntityTranslucentCull(texture);

		if (renderState.getOrDefaultGeckolibData(DataTickets.IS_GLOWING, false)) {
			if (invisible)
				return RenderType.outline(texture);

			return RENDER_TYPE.apply(texture, true, respectLighting);
		}

		return invisible ? null : RENDER_TYPE.apply(texture, false, respectLighting);
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
		super.render(renderState, poseStack, bakedModel, renderType, bufferSource, buffer, LightTexture.FULL_SKY, packedOverlay, renderColor);
	}

	/**
	 * Triplet version of {@link Util#memoize(BiFunction)} for inclusion of a light-sensitive option
	 */
	private static <T, O, L, R> TriFunction<T, O, L, R> memoizeRenderType(final TriFunction<T, O, L, R> function) {
		return new TriFunction<>() {
			private final Map<Triple<T, O, L>, R> cache = new ConcurrentHashMap<>();

			@Override
			public R apply(T texture, O outline, L respectLighting) {
				return this.cache.computeIfAbsent(Triple.of(texture, outline, respectLighting), triple -> function.apply(triple.getLeft(), triple.getMiddle(), triple.getRight()));
			}

			@Override
			public String toString() {
				return "memoize/3[function=" + function + ", size=" + this.cache.size() + "]";
			}
		};
	}

	/**
	 * Create GeckoLib's custom {@link RenderPipeline} for emissive rendering since <code>EYES</code> isn't quite right
	 */
	private static RenderPipeline createRenderPipeline() {
		return RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_FOG_SNIPPET)
				.withLocation(GeckoLibConstants.id("pipeline/emissive"))
				.withVertexShader("core/entity")
				.withFragmentShader("core/entity")
				.withShaderDefine("EMISSIVE")
				.withShaderDefine("NO_OVERLAY")
				.withShaderDefine("NO_CARDINAL_LIGHTING")
				.withSampler("Sampler0")
				.withBlend(BlendFunction.TRANSLUCENT)
				.withDepthWrite(false)
				.withCull(false)
				.withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
				.build();
	}

	/**
	 * Create GeckoLib's custom {@link RenderType} for emissive rendering since <code>EYES</code> isn't quite right
	 */
	private static RenderType createRenderType(ResourceLocation texture, boolean outline, boolean respectLighting) {
		return respectLighting ? RenderType.entityTranslucentEmissive(texture, outline) :
			   RenderType.create("geckolib_emissive",
						  RenderType.TRANSIENT_BUFFER_SIZE,
						  false,
						  true,
						  RENDER_PIPELINE,
						  RenderType.CompositeState.builder()
								  .setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.FALSE, false))
								  .createCompositeState(outline));
	}
}
