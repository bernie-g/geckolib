package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Built-in {@link GeoRenderLayer} for rendering an emissive layer over an existing GeoAnimatable
 * <p>
 * By default, it uses a custom RenderType created by GeckoLib to make things easier, but it can be overridden as needed
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Emissive-Textures-Glow-Layer">GeckoLib Wiki - Glow Layers</a>
 */
public class AutoGlowingGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends TextureLayerGeoLayer<T, O, R> {
	private final Map<ResourceLocation, ResourceLocation> emissiveResourceCache = new Object2ObjectOpenHashMap<>();
	protected static final RenderPipeline RENDER_PIPELINE = RenderPipelines.register(EmissiveRenderType.createRenderPipeline());

	public AutoGlowingGeoLayer(GeoRenderer<T, O, R> renderer) {
		super(renderer);
	}

	/**
	 * Get the texture resource path for the given {@link GeoRenderState}
	 */
	@Override
	protected ResourceLocation getTextureResource(R renderState) {
		return this.emissiveResourceCache.computeIfAbsent(this.renderer.getTextureLocation(renderState), RenderUtil::getEmissiveResource);
	}

	/**
	 * Override to return true if you want your emissive texture to respect lighting in the world
	 * <p>
	 * This will lower its overall brightness slightly but allow it to shade properly
	 * <p>
	 * It may also improve compatibility and/or visual aesthetics when rendering an emissive layer on a translucent model
	 */
	protected boolean shouldRespectWorldLighting(R renderState) {
		return false;
	}

	/**
	 * Override to return true to apply a view-space z-offset to the render buffer
	 * <p>
	 * Typically, you'd use this for armour rendering, but it can be worth trying if you have a custom RenderType object that isn't showing glowmasks
	 */
	protected boolean shouldAddZOffset(R renderState) {
		return getRenderer() instanceof GeoArmorRenderer;
	}

	/**
	 * Override to return a different lighting value if you want to customise the level of emissivity
	 */
	protected int getBrightness(R renderState) {
		return LightTexture.FULL_SKY;
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
		boolean respectLighting = shouldRespectWorldLighting(renderState);
		boolean zOffset = shouldAddZOffset(renderState);

		if (!(renderState instanceof EntityRenderState entityRenderState))
			return EmissiveRenderType.getRenderType(texture, false, respectLighting, zOffset);

		boolean invisible = entityRenderState.isInvisible;

		if (invisible && !renderState.getOrDefaultGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, false))
			return RenderType.itemEntityTranslucentCull(texture);

		if (renderState.getOrDefaultGeckolibData(DataTickets.IS_GLOWING, false)) {
			if (invisible)
				return RenderType.outline(texture);

			return EmissiveRenderType.getRenderType(texture, true, respectLighting, zOffset);
		}

		return invisible ? null : EmissiveRenderType.getRenderType(texture, false, respectLighting, zOffset);
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
		super.render(renderState, poseStack, bakedModel, renderType, bufferSource, buffer, getBrightness(renderState), packedOverlay, renderColor);
	}

	/**
	 * Wrapper class to contain the various methods involved in handling the variants of this GeckoLib's emissive RenderType
	 */
	public static class EmissiveRenderType {
		private static final Map<Entry, RenderType> CACHE = new ConcurrentHashMap<>();

		/**
		 * Get or create a GeckoLib emissive RenderType instance for the given input variables
		 */
		public static RenderType getRenderType(ResourceLocation texture, boolean outline, boolean respectLighting, boolean zOffset) {
			return CACHE.computeIfAbsent(new Entry(texture, outline, respectLighting, zOffset), EmissiveRenderType::buildNewInstance);
		}

		/**
		 * Create GeckoLib's custom {@link RenderPipeline} for emissive rendering since <code>EYES</code> isn't quite right
		 */
		private static RenderPipeline createRenderPipeline() {
			return RenderPipeline.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
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
		private static RenderType buildNewInstance(Entry entry) {
			final RenderType.CompositeState.CompositeStateBuilder compositeStateBuilder = RenderType.CompositeState.builder()
					.setTextureState(new RenderStateShard.TextureStateShard(entry.texture, false))
					.setLayeringState(entry.zOffset ? RenderType.VIEW_OFFSET_Z_LAYERING : RenderStateShard.NO_LAYERING);


			if (entry.respectLighting) {
				return RenderType.create(GeckoLibConstants.MODID + "_entity_translucent_emissive",
										 RenderType.TRANSIENT_BUFFER_SIZE,
										 true,
										 true,
										 RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE,
										 compositeStateBuilder
												 .setOverlayState(RenderType.OVERLAY)
												 .createCompositeState(entry.outline));
			}

			return RenderType.create(GeckoLibConstants.MODID + "_emissive",
									 RenderType.TRANSIENT_BUFFER_SIZE,
									 false,
									 true,
									 RENDER_PIPELINE,
									 compositeStateBuilder.createCompositeState(entry.outline));
		}

		/**
		 * Cached entry key for given input variables and texture
		 */
		public record Entry(ResourceLocation texture, boolean outline, boolean respectLighting, boolean zOffset) {
			@Override
			public int hashCode() {
				return Objects.hash(this.texture, this.outline, this.respectLighting, this.zOffset);
			}
		}
	}
}
