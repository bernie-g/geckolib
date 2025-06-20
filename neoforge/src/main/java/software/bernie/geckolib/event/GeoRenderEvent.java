package software.bernie.geckolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * GeckoLib events base-class for the various event stages of rendering
 * <p>
 * These are fired on the {@link net.neoforged.fml.common.EventBusSubscriber.Bus FORGE} mod bus
 */
public interface GeoRenderEvent<T extends GeoAnimatable, O, R extends GeoRenderState> {
	/**
	 * Returns the renderer for this event
	 *
	 * @see software.bernie.geckolib.renderer.GeoArmorRenderer GeoArmorRenderer
	 * @see software.bernie.geckolib.renderer.GeoBlockRenderer GeoBlockRenderer
	 * @see software.bernie.geckolib.renderer.GeoEntityRenderer GeoEntityRenderer
	 * @see software.bernie.geckolib.renderer.GeoItemRenderer GeoItemRenderer
	 * @see software.bernie.geckolib.renderer.GeoObjectRenderer GeoObjectRenderer
	 * @see software.bernie.geckolib.renderer.GeoReplacedEntityRenderer GeoReplacedEntityRenderer
	 */
	GeoRenderer<T, O, R> getRenderer();

	/**
	 * Returns the GeckoLib render state for the current render pass
	 */
	R getRenderState();

	/**
	 * Shorthand helper for retrieving an attached data element from the {@link GeoRenderState} for this render pass
	 */
	@Nullable
	default <D> D getRenderData(DataTicket<D> dataTicket) {
		final GeoRenderState renderState = getRenderState();

		return renderState.hasGeckolibData(dataTicket) ? getRenderState().getGeckolibData(dataTicket) : null;
	}

	/**
	 * Returns the fraction of a tick that has passed since the last tick as of this render pass
	 */
	default float getPartialTick() {
		return getRenderData(DataTickets.PARTIAL_TICK);
	}

	/**
	 * Returns the {@link LightTexture packed light} value for this render pass
	 */
	default int packedLight() {
		return getRenderData(DataTickets.PACKED_LIGHT);
	}

	/**
	 * Returns the Class the {@link GeoAnimatable} being rendered belongs to
	 */
	default Class<? extends GeoAnimatable> getAnimatableClass() {
		return getRenderData(DataTickets.ANIMATABLE_CLASS);
	}

	/**
	 * Renderer events for {@link BlockEntity BlockEntities} being rendered by {@link GeoBlockRenderer}
	 */
	abstract class Block<T extends BlockEntity & GeoAnimatable> extends Event implements GeoRenderEvent<T, Void, GeoRenderState> {
		private final GeoBlockRenderer<T> renderer;
		private final GeoRenderState renderState;

		public Block(GeoBlockRenderer<T> renderer, GeoRenderState renderState) {
			this.renderer = renderer;
			this.renderState = renderState;
		}

		/**
		 * Returns the GeckoLib render state for the current render pass
		 */
		@Override
		public GeoRenderState getRenderState() {
			return renderState;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoBlockRenderer<T> getRenderer() {
			return this.renderer;
		}

		/**
		 * One-time event for a {@link GeoBlockRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers<T extends BlockEntity & GeoAnimatable> extends Block<T> {
			public CompileRenderLayers(GeoBlockRenderer<T> renderer) {
				super(renderer, null);
			}

			@ApiStatus.Internal
			@Override
			public GeoRenderState getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer<T, Void, GeoRenderState> renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoBlockRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Block} event, or to override/replace data used in rendering
		 */
		public static class CompileRenderState<T extends BlockEntity & GeoAnimatable> extends Block<T> {
			private final T animatable;

			public CompileRenderState(GeoBlockRenderer<T> renderer, GeoRenderState renderState, T animatable) {
				super(renderer, renderState);

				this.animatable = animatable;
			}

			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			public T getAnimatable() {
				return this.animatable;
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			public <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

			/**
			 * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
			 */
			public boolean hasData(DataTicket<?> dataTicket) {
				return getRenderState().hasGeckolibData(dataTicket);
			}
		}

		/**
		 * Pre-render event for block entities being rendered by {@link GeoBlockRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the block entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre<T extends BlockEntity & GeoAnimatable> extends Block<T> implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Pre(GeoBlockRenderer<T> renderer, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}

		/**
		 * Post-render event for block entities being rendered by {@link GeoBlockRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post<T extends BlockEntity & GeoAnimatable> extends Block<T> {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Post(GeoBlockRenderer<T> renderer, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}

			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}
	}

	/**
	 * Renderer events for armor pieces being rendered by {@link GeoArmorRenderer}
	 */
	abstract class Armor<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Event implements GeoRenderEvent<T, GeoArmorRenderer.RenderData, R> {
		private final GeoArmorRenderer<T, R> renderer;
		private final R renderState;

		public Armor(GeoArmorRenderer<T, R> renderer, R renderState) {
			this.renderer = renderer;
			this.renderState = renderState;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoArmorRenderer<T, R> getRenderer() {
			return this.renderer;
		}

		/**
		 * Returns the GeckoLib render state for the current render pass
		 */
		@Override
		public R getRenderState() {
			return this.renderState;
		}

		/**
		 * Shortcut method for retrieving the equipped slot of the armor piece being rendered
		 */
		public EquipmentSlot getEquipmentSlot() {
			return getRenderData(DataTickets.EQUIPMENT_SLOT);
		}

		/**
		 * One-time event for a {@link GeoArmorRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			public CompileRenderLayers(GeoArmorRenderer<T, R> renderer) {
				super(renderer, null);
			}

			@ApiStatus.Internal
			@Override
			public R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer<T, GeoArmorRenderer.RenderData, R> renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoArmorRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Armor} event, or to override/replace data used in rendering
		 */
		public static class CompileRenderState<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			private final T animatable;
			private final GeoArmorRenderer.RenderData renderData;

			public CompileRenderState(GeoArmorRenderer<T, R> renderer, R renderState, T animatable, GeoArmorRenderer.RenderData renderData) {
				super(renderer, renderState);

				this.animatable = animatable;
				this.renderData = renderData;
			}

			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			public T getAnimatable() {
				return this.animatable;
			}

			/**
			 * Get the pre-render data holder that {@link GeoArmorRenderer} uses to build its {@link GeoRenderState}
			 */
			public GeoArmorRenderer.RenderData getRenderData() {
				return this.renderData;
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			public <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

			/**
			 * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
			 */
			public boolean hasData(DataTicket<?> dataTicket) {
				return getRenderState().hasGeckolibData(dataTicket);
			}
		}

		/**
		 * Pre-render event for armor pieces being rendered by {@link GeoArmorRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the armor piece will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Pre(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}

			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}

		/**
		 * Post-render event for armor pieces being rendered by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Post(GeoArmorRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}

			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}
	}

	/**
	 * Renderer events for {@link net.minecraft.world.entity.Entity Entities} being rendered by {@link GeoEntityRenderer}
	 */
	abstract class Entity<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Event implements GeoRenderEvent<T, Void, R> {
		private final GeoEntityRenderer<T, R> renderer;
		private final R renderState;

		public Entity(GeoEntityRenderer<T, R> renderer, R renderState) {
			this.renderer = renderer;
			this.renderState = renderState;
		}

		/**
		 * Returns the GeckoLib render state for the current render pass
		 */
		@Override
		public R getRenderState() {
			return this.renderState;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoEntityRenderer<T, R> getRenderer() {
			return this.renderer;
		}

		/**
		 * One-time event for a {@link GeoEntityRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			public CompileRenderLayers(GeoEntityRenderer<T, R> renderer) {
				super(renderer, null);
			}

			@ApiStatus.Internal
			@Override
			public R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer<T, Void, R> renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Entity} event, or to override/replace data used in rendering
		 */
		public static class CompileRenderState<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			private final T animatable;

			public CompileRenderState(GeoEntityRenderer<T, R> renderer, R renderState, T animatable) {
				super(renderer, renderState);

				this.animatable = animatable;
			}

			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			public T getAnimatable() {
				return this.animatable;
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			public <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

			/**
			 * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
			 */
			public boolean hasData(DataTicket<?> dataTicket) {
				return getRenderState().hasGeckolibData(dataTicket);
			}
		}

		/**
		 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Pre(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}

		/**
		 * Post-render event for entities being rendered by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Post(GeoEntityRenderer<T, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}
	}

	/**
	 * Renderer events for miscellaneous {@link software.bernie.geckolib.animatable.GeoReplacedEntity replaced entities} being rendered by {@link GeoReplacedEntityRenderer}
	 */
	abstract class ReplacedEntity<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends Event implements GeoRenderEvent<T, E, R> {
		private final GeoReplacedEntityRenderer<T, E, R> renderer;
		private final R renderState;

		public ReplacedEntity(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState) {
			this.renderer = renderer;
			this.renderState = renderState;
		}

		/**
		 * Returns the GeckoLib render state for the current render pass
		 */
		@Override
		public R getRenderState() {
			return this.renderState;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoReplacedEntityRenderer<T, E, R> getRenderer() {
			return this.renderer;
		}

		/**
		 * One-time event for a {@link GeoReplacedEntityRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			public CompileRenderLayers(GeoReplacedEntityRenderer<T, E, R> renderer) {
				super(renderer, null);
			}

			@ApiStatus.Internal
			@Override
			public R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer<T, E, R> renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoReplacedEntityRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link ReplacedEntity} event, or to override/replace data used in rendering
		 */
		public static class CompileRenderState<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			private final GeoAnimatable animatable;
			private final E entity;

			public CompileRenderState(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, GeoAnimatable animatable, E entity) {
				super(renderer, renderState);

				this.animatable = animatable;
				this.entity = entity;
			}

			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			public GeoAnimatable getAnimatable() {
				return this.animatable;
			}

			/**
			 * Get the pre-render {@link net.minecraft.world.entity.Entity} that {@link GeoReplacedEntityRenderer} uses to build its {@link GeoRenderState}
			 */
			public net.minecraft.world.entity.Entity getReplacedEntity() {
				return this.entity;
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			public <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

			/**
			 * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
			 */
			public boolean hasData(DataTicket<?> dataTicket) {
				return getRenderState().hasGeckolibData(dataTicket);
			}
		}

		/**
		 * Pre-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Pre(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}

			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}

		/**
		 * Post-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Post(GeoReplacedEntityRenderer<T, E, R> renderer, R renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}

			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}
	}

	/**
	 * Renderer events for {@link ItemStack Items} being rendered by {@link GeoItemRenderer}
	 */
	abstract class Item<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Event implements GeoRenderEvent<T, GeoItemRenderer.RenderData, GeoRenderState> {
		private final GeoItemRenderer<T> renderer;
		private final GeoRenderState renderState;

		public Item(GeoItemRenderer<T> renderer, GeoRenderState renderState) {
			this.renderer = renderer;
			this.renderState = renderState;
		}

		/**
		 * Returns the GeckoLib render state for the current render pass
		 */
		@Override
		public GeoRenderState getRenderState() {
			return this.renderState;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoItemRenderer<T> getRenderer() {
			return this.renderer;
		}

		/**
		 * One-time event for a {@link GeoItemRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			public CompileRenderLayers(GeoItemRenderer<T> renderer) {
				super(renderer, null);
			}

			@ApiStatus.Internal
			@Override
			public GeoRenderState getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer<T, GeoItemRenderer.RenderData, GeoRenderState> renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoArmorRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Armor} event, or to override/replace data used in rendering
		 */
		public static class CompileRenderState<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			private final T animatable;
			private final GeoItemRenderer.RenderData renderData;

			public CompileRenderState(GeoItemRenderer<T> renderer, GeoRenderState renderState, T animatable, GeoItemRenderer.RenderData renderData) {
				super(renderer, renderState);

				this.animatable = animatable;
				this.renderData = renderData;
			}

			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			public T getAnimatable() {
				return this.animatable;
			}

			/**
			 * Get the associated render data for this render pass
			 */
			public GeoItemRenderer.RenderData getRenderData() {
				return this.renderData;
			}

			/**
			 * Get the pre-render ItemStack that {@link GeoItemRenderer} uses to build its {@link GeoRenderState}
			 */
			public ItemStack getItemStack() {
				return this.renderData.itemStack();
			}

			/**
			 * Get the render perspective for the render pass
			 */
			public ItemDisplayContext getRenderPerspective() {
				return this.renderData.renderPerspective();
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			public <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

			/**
			 * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
			 */
			public boolean hasData(DataTicket<?> dataTicket) {
				return getRenderState().hasGeckolibData(dataTicket);
			}
		}

		/**
		 * Pre-render event for armor being rendered by {@link GeoItemRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the ItemStack will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Pre(GeoItemRenderer<T> renderer, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}

		/**
		 * Post-render event for ItemStacks being rendered by {@link GeoItemRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Post(GeoItemRenderer<T> renderer, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}
	}

	/**
	 * Renderer events for miscellaneous {@link GeoAnimatable animatables} being rendered by {@link GeoObjectRenderer}
	 */
	abstract class Object<T extends GeoAnimatable> extends Event implements GeoRenderEvent<T, Void, GeoRenderState> {
		private final GeoObjectRenderer<T> renderer;
		private final GeoRenderState renderState;

		public Object(GeoObjectRenderer<T> renderer, GeoRenderState renderState) {
			this.renderer = renderer;
			this.renderState = renderState;
		}

		/**
		 * Returns the GeckoLib render state for the current render pass
		 */
		@Override
		public GeoRenderState getRenderState() {
			return this.renderState;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoObjectRenderer<T> getRenderer() {
			return this.renderer;
		}

		/**
		 * One-time event for a {@link GeoObjectRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers<T extends GeoAnimatable> extends Object<T> {
			public CompileRenderLayers(GeoObjectRenderer<T> renderer) {
				super(renderer, null);
			}

			@ApiStatus.Internal
			@Override
			public GeoRenderState getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer<T, Void, GeoRenderState> renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoObjectRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Armor} event, or to override/replace data used in rendering
		 */
		public static class CompileRenderState<T extends GeoAnimatable> extends Object<T> {
			private final T animatable;

			public CompileRenderState(GeoObjectRenderer<T> renderer, GeoRenderState renderState, T animatable) {
				super(renderer, renderState);

				this.animatable = animatable;
			}

			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			public T getAnimatable() {
				return this.animatable;
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			public <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

			/**
			 * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
			 */
			public boolean hasData(DataTicket<?> dataTicket) {
				return getRenderState().hasGeckolibData(dataTicket);
			}
		}

		/**
		 * Pre-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is {@link ICancellableEvent Cancelable}<br>
		 * If the event is cancelled, the object will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre<T extends GeoAnimatable> extends Object<T> implements ICancellableEvent {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Pre(GeoObjectRenderer<T> renderer, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}

		/**
		 * Post-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}
		 <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post<T extends GeoAnimatable> extends Object<T> {
			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;

			public Post(GeoObjectRenderer<T> renderer, GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource) {
				super(renderer, renderState);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
			}

			public PoseStack getPoseStack() {
				return this.poseStack;
			}
			
			public BakedGeoModel getModel() {
				return this.model;
			}

			public MultiBufferSource getBufferSource() {
				return this.bufferSource;
			}
		}
	}
}
