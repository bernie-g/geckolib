package software.bernie.geckolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import javax.annotation.Nullable;

/**
 * GeckoLib events base-class for the various event stages of rendering.<br>
 */
public interface GeoRenderEvent {
	/**
	 * Returns the renderer for this event
	 * @see software.bernie.geckolib.renderer.DynamicGeoEntityRenderer DynamicGeoEntityRenderer
	 * @see software.bernie.geckolib.renderer.GeoArmorRenderer GeoArmorRenderer
	 * @see software.bernie.geckolib.renderer.GeoBlockRenderer GeoBlockRenderer
	 * @see software.bernie.geckolib.renderer.GeoEntityRenderer GeoEntityRenderer
	 * @see software.bernie.geckolib.animatable.GeoItem GeoItem
	 * @see software.bernie.geckolib.renderer.GeoObjectRenderer GeoObjectRenderer
	 * @see software.bernie.geckolib.renderer.GeoReplacedEntityRenderer GeoReplacedEntityRenderer
	 */
	GeoRenderer<?> getRenderer();

	/**
	 * Renderer events for armor pieces being rendered by {@link GeoArmorRenderer}
	 */
	abstract class Armor implements GeoRenderEvent {
		private final GeoArmorRenderer<?> renderer;

		public Armor(GeoArmorRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoArmorRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the entity being rendered
		 */
		@Nullable
		public net.minecraft.world.entity.Entity getEntity() {
			return getRenderer().getCurrentEntity();
		}

		/**
		 * Shortcut method for retrieving the ItemStack relevant to the armor piece being rendered
		 */
		@Nullable
		public ItemStack getItemStack() {
			return getRenderer().getCurrentStack();
		}

		/**
		 * Shortcut method for retrieving the equipped slot of the armor piece being rendered
		 */
		@Nullable
		public EquipmentSlot getEquipmentSlot() {
			return getRenderer().getCurrentSlot();
		}

		/**
		 * Pre-render event for armor pieces being rendered by {@link GeoArmorRenderer}.<br>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}<br>
		 * <br>
		 * This event is <u>cancellable</u>.<br>
		 * If the event is cancelled by returning false in the {@link Listener}, the armor piece will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Armor {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
				for (Listener listener : listeners) {
					if (!listener.handle(event))
						return false;
				}

				return true;
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Armor.Pre GeoRenderEvent.<br>
			 * Return false to cancel the render pass
			 */
			@FunctionalInterface
			public interface Listener {
				boolean handle(Pre event);
			}
		}

		/**
		 * Post-render event for armor pieces being rendered by {@link GeoEntityRenderer}.<br>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post extends Armor {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Armor.Post GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(Post event);
			}
		}

		/**
		 * One-time event for a {@link GeoArmorRenderer} called on first initialisation.<br>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Armor {
			public static final Event<CompileRenderLayers.Listener> EVENT = EventFactory.createArrayBacked(CompileRenderLayers.Listener.class, post -> {}, listeners -> event -> {
				for (CompileRenderLayers.Listener listener : listeners) {
					listener.handle(event);
				}
			});

			public CompileRenderLayers(GeoArmorRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer.<br>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}

			/**
			 * Event listener interface for the Armor.CompileRenderLayers GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(CompileRenderLayers event);
			}
		}
	}

	/**
	 * Renderer events for {@link BlockEntity BlockEntities} being rendered by {@link GeoBlockRenderer}
	 */
	abstract class Block implements GeoRenderEvent {
		private final GeoBlockRenderer<?> renderer;

		public Block(GeoBlockRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoBlockRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the block entity being rendered
		 */
		public BlockEntity getBlockEntity() {
			return getRenderer().getAnimatable();
		}

		/**
		 * Pre-render event for block entities being rendered by {@link GeoBlockRenderer}.<br>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}<br>
		 * <br>
		 * This event is <u>cancellable</u>.<br>
		 * If the event is cancelled by returning false in the {@link Listener}, the block entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Block {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
				for (Listener listener : listeners) {
					if (!listener.handle(event))
						return false;
				}

				return true;
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Block.Pre GeoRenderEvent.<br>
			 * Return false to cancel the render pass
			 */
			@FunctionalInterface
			public interface Listener {
				boolean handle(Pre event);
			}
		}

		/**
		 * Post-render event for block entities being rendered by {@link GeoBlockRenderer}.<br>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post extends Block {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Block.Post GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(Post event);
			}
		}

		/**
		 * One-time event for a {@link GeoBlockRenderer} called on first initialisation.<br>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Block {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			public CompileRenderLayers(GeoBlockRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer.<br>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}

			/**
			 * Event listener interface for the Armor.CompileRenderLayers GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(CompileRenderLayers event);
			}
		}
	}

	/**
	 * Renderer events for {@link net.minecraft.world.entity.Entity Entities} being rendered by {@link GeoEntityRenderer}, as well as
	 * {@link software.bernie.geckolib.renderer.DynamicGeoEntityRenderer DynamicGeoEntityRenderer}
	 */
	abstract class Entity implements GeoRenderEvent {
		private final GeoEntityRenderer<?> renderer;

		public Entity(GeoEntityRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoEntityRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the entity being rendered
		 */
		public net.minecraft.world.entity.Entity getEntity() {
			return this.renderer.getAnimatable();
		}

		/**
		 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}.<br>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}<br>
		 * <br>
		 * This event is <u>cancellable</u>.<br>
		 * If the event is cancelled by returning false in the {@link Listener}, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Entity {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
				for (Listener listener : listeners) {
					if (!listener.handle(event))
						return false;
				}

				return true;
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Armor.Pre GeoRenderEvent.<br>
			 * Return false to cancel the render pass
			 */
			@FunctionalInterface
			public interface Listener {
				boolean handle(Pre event);
			}
		}

		/**
		 * Post-render event for entities being rendered by {@link GeoEntityRenderer}.<br>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post extends Entity {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Entity.Post GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(Post event);
			}
		}

		/**
		 * One-time event for a {@link GeoEntityRenderer} called on first initialisation.<br>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Entity {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			public CompileRenderLayers(GeoEntityRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer.<br>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}

			/**
			 * Event listener interface for the Entity.CompileRenderLayers GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(CompileRenderLayers event);
			}
		}
	}

	/**
	 * Renderer events for {@link ItemStack Items} being rendered by {@link GeoItemRenderer}
	 */
	abstract class Item implements GeoRenderEvent {
		private final GeoItemRenderer<?> renderer;

		public Item(GeoItemRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoItemRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method for retrieving the ItemStack being rendered
		 */
		public ItemStack getItemStack() {
			return getRenderer().getCurrentItemStack();
		}

		/**
		 * Pre-render event for armor being rendered by {@link GeoItemRenderer}.<br>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}<br>
		 * <br>
		 * This event is <u>cancellable</u>.<br>
		 * If the event is cancelled by returning false in the {@link Listener}, the ItemStack will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Item {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
				for (Listener listener : listeners) {
					if (!listener.handle(event))
						return false;
				}

				return true;
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Item.Pre GeoRenderEvent.<br>
			 * Return false to cancel the render pass
			 */
			@FunctionalInterface
			public interface Listener {
				boolean handle(Pre event);
			}
		}

		/**
		 * Post-render event for ItemStacks being rendered by {@link GeoItemRenderer}.<br>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post extends Item {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Item.Post GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(Post event);
			}
		}

		/**
		 * One-time event for a {@link GeoItemRenderer} called on first initialisation.<br>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Item {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			public CompileRenderLayers(GeoItemRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer.<br>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}

			/**
			 * Event listener interface for the Item.CompileRenderLayers GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(CompileRenderLayers event);
			}
		}
	}

	/**
	 * Renderer events for miscellaneous {@link software.bernie.geckolib.core.animatable.GeoAnimatable animatables} being rendered by {@link GeoObjectRenderer}
	 */
	abstract class Object implements GeoRenderEvent {
		private final GeoObjectRenderer<?> renderer;

		public Object(GeoObjectRenderer<?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoObjectRenderer<?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Pre-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}.<br>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}<br>
		 * <br>
		 * This event is <u>cancellable</u>.<br>
		 * If the event is cancelled by returning false in the {@link Listener}, the object will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends Object {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
				for (Listener listener : listeners) {
					if (!listener.handle(event))
						return false;
				}

				return true;
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Object.Pre GeoRenderEvent.<br>
			 * Return false to cancel the render pass
			 */
			@FunctionalInterface
			public interface Listener {
				boolean handle(Pre event);
			}
		}

		/**
		 * Post-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}.<br>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post extends Object {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the Object.Post GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(Post event);
			}
		}

		/**
		 * One-time event for a {@link GeoObjectRenderer} called on first initialisation.<br>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends Object {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			public CompileRenderLayers(GeoObjectRenderer<?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer.<br>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}

			/**
			 * Event listener interface for the Object.CompileRenderLayers GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(CompileRenderLayers event);
			}
		}
	}

	/**
	 * Renderer events for miscellaneous {@link software.bernie.geckolib.animatable.GeoReplacedEntity replaced entities} being rendered by {@link GeoReplacedEntityRenderer}
	 */
	abstract class ReplacedEntity implements GeoRenderEvent {
		private final GeoReplacedEntityRenderer<?, ?> renderer;

		public ReplacedEntity(GeoReplacedEntityRenderer<?, ?> renderer) {
			this.renderer = renderer;
		}

		/**
		 * Returns the renderer for this event
		 */
		@Override
		public GeoReplacedEntityRenderer<?, ?> getRenderer() {
			return this.renderer;
		}

		/**
		 * Shortcut method to get the Entity currently being rendered
		 */
		public net.minecraft.world.entity.Entity getReplacedEntity() {
			return getRenderer().getCurrentEntity();
		}

		/**
		 * Pre-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer<?, ?>}.<br>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}<br>
		 * <br>
		 * This event is <u>cancellable</u>.<br>
		 * If the event is cancelled by returning false in the {@link Listener}, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		public static class Pre extends ReplacedEntity {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, event -> true, listeners -> event -> {
				for (Listener listener : listeners) {
					if (!listener.handle(event))
						return false;
				}

				return true;
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Pre(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the ReplacedEntity.Pre GeoRenderEvent.<br>
			 * Return false to cancel the render pass
			 */
			@FunctionalInterface
			public interface Listener {
				boolean handle(Pre event);
			}
		}

		/**
		 * Post-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer}.<br>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		public static class Post extends ReplacedEntity {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			private final PoseStack poseStack;
			private final BakedGeoModel model;
			private final MultiBufferSource bufferSource;
			private final float partialTick;
			private final int packedLight;

			public Post(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
				super(renderer);

				this.poseStack = poseStack;
				this.model = model;
				this.bufferSource = bufferSource;
				this.partialTick = partialTick;
				this.packedLight = packedLight;
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

			public float getPartialTick() {
				return this.partialTick;
			}

			public int getPackedLight() {
				return this.packedLight;
			}

			/**
			 * Event listener interface for the ReplacedEntity.Post GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(Post event);
			}
		}

		/**
		 * One-time event for a {@link GeoReplacedEntityRenderer} called on first initialisation.<br>
		 * Use this event to add render layers to the renderer as needed
		 */
		public static class CompileRenderLayers extends ReplacedEntity {
			public static final Event<Listener> EVENT = EventFactory.createArrayBacked(Listener.class, post -> {}, listeners -> event -> {
				for (Listener listener : listeners) {
					listener.handle(event);
				}
			});

			public CompileRenderLayers(GeoReplacedEntityRenderer<?, ?> renderer) {
				super(renderer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer.<br>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			public void addLayer(GeoRenderLayer renderLayer) {
				getRenderer().addRenderLayer(renderLayer);
			}

			/**
			 * Event listener interface for the ReplacedEntity.CompileRenderLayers GeoRenderEvent
			 */
			@FunctionalInterface
			public interface Listener {
				void handle(CompileRenderLayers event);
			}
		}
	}
}
