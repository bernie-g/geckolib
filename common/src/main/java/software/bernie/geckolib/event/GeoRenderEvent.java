package software.bernie.geckolib.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import java.util.function.Function;

/**
 * GeckoLib events base-class for the various event stages of rendering
 * <p>
 * This interface exists for multiloader-friendly event handling.<br>
 * The actual event objects are loader-dependent.
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
     * Get the existing data for the given {@link DataTicket}.
     * <p>
     * Note that the data itself may be null - use {@link #hasData} to differentiate non-existent data from null data if necessary.
     *
     * @param dataTicket The DataTicket denoting the data to be retrieved
     * @return The data associated with the DataTicket, or null if there is no existing data
     */
	@Nullable
	default <D> D getRenderData(DataTicket<D> dataTicket) {
		final GeoRenderState renderState = getRenderState();

        return renderState.getOrDefaultGeckolibData(dataTicket, null);
	}

    /**
     * @return Whether the {@link GeoRenderState} has data associated with the given {@link DataTicket}
     */
    default boolean hasData(DataTicket<?> dataTicket) {
        return getRenderState().hasGeckolibData(dataTicket);
    }

	/**
	 * Returns the fraction of a tick that has passed since the last tick as of this render pass
	 */
	default float getPartialTick() {
		return getRenderState().getPartialTick();
	}

	/**
	 * Returns the {@link LightTexture packed light} value for this render pass
	 */
	default int packedLight() {
		return getRenderState().getPackedLight();
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
	interface Block<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends GeoRenderEvent<T, Void, R> {
        /**
         * Returns the renderer for this event
         */
        @Override
        GeoBlockRenderer<T, R> getRenderer();

		/**
		 * One-time event for a {@link GeoBlockRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		interface CompileRenderLayers<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(Function<GeoBlockRenderer<T, R>, GeoRenderLayer<T, Void, R>> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(GeoRenderLayer<T, Void, R> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }
		}

		/**
		 * Pre-render event for blocks being rendered by {@link GeoBlockRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Block} event, or to override/replace data used in rendering
		 */
		interface CompileRenderState<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			T getAnimatable();

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
            default <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
                getRenderState().addGeckolibData(dataTicket, data);
            }
		}

		/**
		 * Pre-render event for block entities being rendered by {@link GeoBlockRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
		 * <p>
		 * This event is cancellable.<br>
		 * If the event is cancelled, the block entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		interface Pre<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
			PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
			BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
			SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
			CameraRenderState getCameraState();
		}

		/**
		 * Post-render event for block entities being rendered by {@link GeoBlockRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
         * <p>
         * Because of the batching Minecraft uses for rendering, nothing has actually been rendered at this stage, and further rendering operations should be
         * submitted to the {@link SubmitNodeCollector} returned by {@link #getRenderTasks()}.
		 */
		interface Post<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}
	}

	/**
	 * Renderer events for armor pieces being rendered by {@link GeoArmorRenderer}
	 */
	interface Armor<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends GeoRenderEvent<T, GeoArmorRenderer.RenderData, R> {
        /**
         * Returns the renderer for this event
         */
        @Override
        GeoArmorRenderer<T, R> getRenderer();

		/**
		 * One-time event for a {@link GeoArmorRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		interface CompileRenderLayers<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(Function<GeoArmorRenderer<T, R>, GeoRenderLayer<T, GeoArmorRenderer.RenderData, R>> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(GeoRenderLayer<T, GeoArmorRenderer.RenderData, R> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }
		}

		/**
		 * Pre-render event for armor pieces being renderd by {@link GeoArmorRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Armor} event, or to override/replace data used in rendering
		 */
		interface CompileRenderState<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			T getAnimatable();

			/**
			 * Get the pre-render data holder that {@link GeoArmorRenderer} uses to build its {@link GeoRenderState}
			 */
			GeoArmorRenderer.RenderData getRenderData();

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
            default <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
                getRenderState().addGeckolibData(dataTicket, data);
            }
		}

		/**
		 * Pre-render event for armor pieces being rendered by {@link GeoArmorRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
         * <p>
         * This event is cancellable.<br>
         * If the event is cancelled, the armor piece will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		interface Pre<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}

		/**
		 * Post-render event for armor pieces being rendered by {@link GeoArmorRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		interface Post<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}
	}

	/**
	 * Renderer events for {@link net.minecraft.world.entity.Entity Entities} being rendered by {@link GeoEntityRenderer}
	 */
	interface Entity<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoRenderEvent<T, Void, R> {
        /**
         * Returns the renderer for this event
         */
        @Override
        GeoEntityRenderer<T, R> getRenderer();

		/**
		 * One-time event for a {@link GeoEntityRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		interface CompileRenderLayers<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(Function<GeoEntityRenderer<T, R>, GeoRenderLayer<T, Void, R>> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(GeoRenderLayer<T, Void, R> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }
		}

		/**
		 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Entity} event, or to override/replace data used in rendering
		 */
		interface CompileRenderState<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			T getAnimatable();

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			default <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}
		}

		/**
		 * Pre-render event for entities being rendered by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
         * <p>
         * This event is cancellable.<br>
         * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		interface Pre<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}

		/**
		 * Post-render event for entities being rendered by {@link GeoEntityRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		interface Post<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}
	}

	/**
	 * Renderer events for miscellaneous {@link software.bernie.geckolib.animatable.GeoReplacedEntity replaced entities} being rendered by {@link GeoReplacedEntityRenderer}
	 */
	interface ReplacedEntity<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends GeoRenderEvent<T, E, R> {
        /**
         * Returns the renderer for this event
         */
        @Override
        GeoReplacedEntityRenderer<T, E, R> getRenderer();

		/**
		 * One-time event for a {@link GeoReplacedEntityRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		interface CompileRenderLayers<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(Function<GeoReplacedEntityRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(GeoRenderLayer<T, E, R> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for armor pieces being rendered by {@link GeoReplacedEntityRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link ReplacedEntity} event, or to override/replace data used in rendering
		 */
		interface CompileRenderState<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			GeoAnimatable getAnimatable();

			/**
			 * Get the pre-render {@link net.minecraft.world.entity.Entity} that {@link GeoReplacedEntityRenderer} uses to build its {@link GeoRenderState}
			 */
			net.minecraft.world.entity.Entity getReplacedEntity();

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
            default <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
                getRenderState().addGeckolibData(dataTicket, data);
            }
		}

		/**
		 * Pre-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
         * <p>
         * This event is cancellable.<br>
         * If the event is cancelled, the entity will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		interface Pre<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}

		/**
		 * Post-render event for replaced entities being rendered by {@link GeoReplacedEntityRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		interface Post<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}
	}

	/**
	 * Renderer events for {@link ItemStack Items} being rendered by {@link GeoItemRenderer}
	 */
	interface Item<T extends net.minecraft.world.item.Item & GeoAnimatable> extends GeoRenderEvent<T, GeoItemRenderer.RenderData, GeoRenderState> {
		/**
		 * Returns the renderer for this event
		 */
		@Override
		GeoItemRenderer<T> getRenderer();

		/**
		 * One-time event for a {@link GeoItemRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		interface CompileRenderLayers<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			@ApiStatus.Internal
			@Override
			default GeoRenderState getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(Function<GeoItemRenderer<T>, GeoRenderLayer<T, GeoItemRenderer.RenderData, GeoRenderState>> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(GeoRenderLayer<T, GeoItemRenderer.RenderData, GeoRenderState> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for items being rendered by {@link GeoItemRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Item} event, or to override/replace data used in rendering
		 */
		interface CompileRenderState<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			T getAnimatable();

			/**
			 * Get the associated render data for this render pass
			 */
			GeoItemRenderer.RenderData getRenderData();

			/**
			 * Get the pre-render ItemStack that {@link GeoItemRenderer} uses to build its {@link GeoRenderState}
			 */
			default ItemStack getItemStack() {
				return getRenderData().itemStack();
			}

			/**
			 * Get the render perspective for the render pass
			 */
			default ItemDisplayContext getRenderPerspective() {
				return getRenderData().renderPerspective();
			}

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			default <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}
		}

		/**
		 * Pre-render event for items being rendered by {@link GeoItemRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
         * <p>
         * This event is cancellable.<br>
         * If the event is cancelled, the item will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		interface Pre<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}

		/**
		 * Post-render event for items being rendered by {@link GeoItemRenderer}
		 * <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		interface Post<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}
	}

	/**
	 * Renderer events for miscellaneous {@link GeoAnimatable animatables} being rendered by {@link GeoObjectRenderer}
	 */
	interface Object<T extends GeoAnimatable, E, R extends GeoRenderState> extends GeoRenderEvent<T, E, R> {
		/**
		 * Returns the renderer for this event
		 */
		@Override
		GeoObjectRenderer<T, E, R> getRenderer();

		/**
		 * One-time event for a {@link GeoObjectRenderer} called on first initialisation
		 * <p>
		 * Use this event to add render layers to the renderer as needed
		 */
		interface CompileRenderLayers<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(Function<GeoObjectRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}

			/**
			 * Adds a {@link GeoRenderLayer} to the renderer
			 * <p>
			 * Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			 */
			default void addLayer(GeoRenderLayer<T, E, R> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}
		}

		/**
		 * Pre-render event for objects being rendered by {@link GeoObjectRenderer}
		 * <p>
		 * This event is called in preparation for rendering, when the renderer is gathering data to pass through
		 * <p>
		 * Use this event to add data that you may need in a later {@link Object} event, or to override/replace data used in rendering
		 */
		interface CompileRenderState<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
			/**
			 * Get the GeoAnimatable instance relevant to the {@link GeoRenderState} being compiled
			 */
			T getAnimatable();

			/**
			 * Add additional data to the {@link GeoRenderState}
			 *
			 * @param dataTicket The DataTicket denoting the data to be added
			 * @param data The data to be added
			 */
			default <D> void addData(DataTicket<D> dataTicket, @Nullable D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

            /**
             * Get the associated render data object for this render pass, or null if not applicable
             */
            @Nullable
            E getRelatedObject();
		}

		/**
		 * Pre-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}
		 * <p>
		 * This event is called before rendering, but after {@link GeoRenderer#preRender}
         * <p>
         * This event is cancellable.<br>
         * If the event is cancelled, the object will not be rendered and the corresponding {@link Post} event will not be fired.
		 */
		interface Pre<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}

		/**
		 * Post-render event for miscellaneous animatables being rendered by {@link GeoObjectRenderer}
		 <p>
		 * This event is called after {@link GeoRenderer#postRender}
		 */
		interface Post<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
            /**
             * Get the PoseStack for the current render pass.<br>
             * The renderer has not yet scaled or positioned the PoseStack as this stage.
             */
            PoseStack getPoseStack();

            /**
             * @return The baked model for this render pass
             */
            BakedGeoModel getModel();

            /**
             * @return The render submission collector for this render pass
             */
            SubmitNodeCollector getRenderTasks();

            /**
             * @return The camera render state for this render pass
             */
            CameraRenderState getCameraState();
		}
	}
}
