package com.geckolib.event;

import com.geckolib.animatable.GeoReplacedEntity;
import com.geckolib.renderer.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Brightness;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoItem;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.*;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Objects;
import java.util.function.Function;

/// GeckoLib events base-class for the various event stages of rendering
///
/// This interface exists for multiloader-friendly event handling.
/// The actual event objects are loader-dependent.
///
/// @param <T> Animatable class type
/// @param <O> Associated object class type, or [Void] if none
/// @param <R> RenderState class type
public interface GeoRenderEvent<T extends GeoAnimatable, O, R extends GeoRenderState> {
	/// Returns the renderer for this event
	///
	/// @see GeoArmorRenderer GeoArmorRenderer
	/// @see GeoBlockRenderer GeoBlockRenderer
	/// @see GeoEntityRenderer GeoEntityRenderer
	/// @see GeoItemRenderer GeoItemRenderer
	/// @see GeoObjectRenderer GeoObjectRenderer
	/// @see GeoReplacedEntityRenderer GeoReplacedEntityRenderer
	GeoRenderer<T, O, R> getRenderer();

	/// @return The GeckoLib render state for the current render pass
	R getRenderState();

    /// Get the existing data for the given [DataTicket].
    ///
    /// Note that the data itself may be null - use [#hasData] to differentiate non-existent data from null data if necessary.
    ///
    /// @param dataTicket The DataTicket denoting the data to be retrieved
    /// @return The data associated with the DataTicket, or null if there is no existing data
    default <D> @Nullable D getRenderData(DataTicket<D> dataTicket) {
		final GeoRenderState renderState = getRenderState();

        return renderState.getGeckolibData(dataTicket);
	}

    /// @return Whether the [GeoRenderState] has data associated with the given [DataTicket]
    default boolean hasData(DataTicket<?> dataTicket) {
        return getRenderState().hasGeckolibData(dataTicket);
    }

	/// Returns the fraction of a tick that has passed since the last tick as of this render pass
	default float getPartialTick() {
		return getRenderState().getPartialTick();
	}

	/// Returns the [packed light][Brightness] value for this render pass
	default int packedLight() {
		return getRenderState().getPackedLight();
	}

	/// Returns the Class the [GeoAnimatable] being rendered belongs to
	default Class<? extends GeoAnimatable> getAnimatableClass() {
        //noinspection unchecked
        return Objects.requireNonNull(getRenderData(DataTickets.ANIMATABLE_CLASS));
	}

	/// Renderer events for [BlockEntities][BlockEntity] being rendered by [GeoBlockRenderer]
	///
	/// @param <T> BlockEntity animatable class type
	/// @param <R> RenderState class type
	interface Block<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends GeoRenderEvent<T, Void, R> {
        /// Returns the renderer for this event
        @Override
        GeoBlockRenderer<T, R> getRenderer();

		/// One-time event for a [GeoBlockRenderer] called on first initialization
		///
		/// Use this event to add render layers to the renderer as needed
		///
		/// @param <T> BlockEntity animatable class type
		/// @param <R> RenderState class type
		interface CompileRenderLayers<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(Function<GeoBlockRenderer<T, R>, GeoRenderLayer<T, Void, R>> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(GeoRenderLayer<T, Void, R> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }
		}

		/// Pre-render event for blocks being rendered by [GeoBlockRenderer]
		///
		/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
		///
		/// Use this event to add data that you may need in a later [Block] event, or to override/replace data used in rendering
		///
		/// @param <T> BlockEntity animatable class type
		/// @param <R> RenderState class type
		interface CompileRenderState<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
			/// Get the GeoAnimatable instance relevant to the [GeoRenderState] being compiled
			T getAnimatable();

			/// Add additional data to the [GeoRenderState]
			///
			/// @param dataTicket The DataTicket denoting the data to be added
			/// @param data The data to be added
			default <D> void addData(DataTicket<D> dataTicket, D data) {
                getRenderState().addGeckolibData(dataTicket, data);
            }
		}

		/// Pre-render event for BlockEntities being rendered by [GeoBlockRenderer]
		///
		/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
		///
		/// This event is cancelable.
		/// If the event is canceled, the BlockEntity will not be rendered.
		///
		/// @param <T> BlockEntity animatable class type
		/// @param <R> RenderState class type
		interface Pre<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState> extends Block<T, R> {
            /// Returns the render pass info for the current render pass for this animatable
            RenderPassInfo<R> getRenderPassInfo();

            /// Get the PoseStack for the current render pass.
            /// The renderer has not yet scaled or positioned the PoseStack as this stage.
            PoseStack getPoseStack();

            /// @return The baked model for this render pass
            BakedGeoModel getModel();

            /// @return The render submission collector for this render pass
            SubmitNodeCollector getRenderTasks();

            /// @return The camera render state for this render pass
            CameraRenderState getCameraState();
		}
	}

	/// Renderer events for armor pieces being rendered by [GeoArmorRenderer]
	///
	/// @param <T> Item animatable class type
	/// @param <R> RenderState class type
	interface Armor<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends GeoRenderEvent<T, GeoArmorRenderer.RenderData, R> {
        /// Returns the renderer for this event
        @Override
        GeoArmorRenderer<T, R> getRenderer();

		/// One-time event for a [GeoArmorRenderer] called on first initialization
		///
		/// Use this event to add render layers to the renderer as needed
		///
		/// @param <T> Item animatable class type
		/// @param <R> RenderState class type
		interface CompileRenderLayers<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(Function<GeoArmorRenderer<T, R>, GeoRenderLayer<T, GeoArmorRenderer.RenderData, R>> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(GeoRenderLayer<T, GeoArmorRenderer.RenderData, R> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }
		}

		/// Pre-render event for armor pieces being renderd by [GeoArmorRenderer]
		///
		/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
		///
		/// Use this event to add data that you may need in a later [Armor] event, or to override/replace data used in rendering
		///
		/// @param <T> Item animatable class type
		/// @param <R> RenderState class type
		interface CompileRenderState<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
			/// Get the GeoAnimatable instance relevant to the [GeoRenderState] being compiled
			T getAnimatable();

			/// Get the pre-render data holder that [GeoArmorRenderer] uses to build its [GeoRenderState]
			GeoArmorRenderer.RenderData getRenderData();

			/// Add additional data to the [GeoRenderState]
			///
			/// @param dataTicket The DataTicket denoting the data to be added
			/// @param data The data to be added
			default <D> void addData(DataTicket<D> dataTicket, D data) {
                getRenderState().addGeckolibData(dataTicket, data);
            }
		}

		/// Pre-render event for armor pieces being rendered by [GeoArmorRenderer]
		///
		/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
		///
		/// This event is cancellable.
		/// If the event is canceled, the armor piece will not be rendered.
		///
		/// @param <T> Item animatable class type
		/// @param <R> RenderState class type
		interface Pre<T extends net.minecraft.world.item.Item & GeoItem, R extends HumanoidRenderState & GeoRenderState> extends Armor<T, R> {
            /// Returns the render pass info for the current render pass for this animatable
            RenderPassInfo<R> getRenderPassInfo();

            /// Get the PoseStack for the current render pass.
            /// The renderer has not yet scaled or positioned the PoseStack as this stage.
            PoseStack getPoseStack();

            /// @return The baked model for this render pass
            BakedGeoModel getModel();

            /// @return The render submission collector for this render pass
            SubmitNodeCollector getRenderTasks();

            /// @return The camera render state for this render pass
            CameraRenderState getCameraState();
		}
	}

	/// Renderer events for [Entities][net.minecraft.world.entity.Entity] being rendered by [GeoEntityRenderer]
	///
	/// @param <T> Entity animatable class type
	/// @param <R> RenderState class type
	interface Entity<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends GeoRenderEvent<T, Void, R> {
        /// Returns the renderer for this event
        @Override
        GeoEntityRenderer<T, R> getRenderer();

		/// One-time event for a [GeoEntityRenderer] called on first initialization
		///
		/// Use this event to add render layers to the renderer as needed
		///
		/// @param <T> Entity animatable class type
		/// @param <R> RenderState class type
		interface CompileRenderLayers<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(Function<GeoEntityRenderer<T, R>, GeoRenderLayer<T, Void, R>> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(GeoRenderLayer<T, Void, R> renderLayer) {
                getRenderer().withRenderLayer(renderLayer);
            }
		}

		/// Pre-render event for entities being rendered by [GeoEntityRenderer]
		///
		/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
		///
		/// Use this event to add data that you may need in a later [Entity] event, or to override/replace data used in rendering
		///
		/// @param <T> Entity animatable class type
		/// @param <R> RenderState class type
		interface CompileRenderState<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
			/// Get the GeoAnimatable instance relevant to the [GeoRenderState] being compiled
			T getAnimatable();

			/// Add additional data to the [GeoRenderState]
			///
			/// @param dataTicket The DataTicket denoting the data to be added
			/// @param data The data to be added
			default <D> void addData(DataTicket<D> dataTicket, D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}
		}

		/// Pre-render event for entities being rendered by [GeoEntityRenderer]
		///
		/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
		///
		/// This event is cancelable.
		/// If the event is canceled, the entity will not be rendered.
		///
		/// @param <T> Entity animatable class type
		/// @param <R> RenderState class type
		interface Pre<T extends net.minecraft.world.entity.Entity & GeoAnimatable, R extends EntityRenderState & GeoRenderState> extends Entity<T, R> {
            /// Returns the render pass info for the current render pass for this animatable
            RenderPassInfo<R> getRenderPassInfo();

            /// Get the PoseStack for the current render pass.
            /// The renderer has not yet scaled or positioned the PoseStack as this stage.
            PoseStack getPoseStack();

            /// @return The baked model for this render pass
            BakedGeoModel getModel();

            /// @return The render submission collector for this render pass
            SubmitNodeCollector getRenderTasks();

            /// @return The camera render state for this render pass
            CameraRenderState getCameraState();
		}
	}

	/// Renderer events for miscellaneous [replaced entities][GeoReplacedEntity] being rendered by [GeoReplacedEntityRenderer]
	///
	/// @param <T> Entity animatable class type. This is the animatable being rendered
	/// @param <E> Entity class type. This is the entity being replaced
	/// @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
	interface ReplacedEntity<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends GeoRenderEvent<T, E, R> {
        /// Returns the renderer for this event
        @Override
        GeoReplacedEntityRenderer<T, E, R> getRenderer();

		/// One-time event for a [GeoReplacedEntityRenderer] called on first initialization
		///
		/// Use this event to add render layers to the renderer as needed
		///
		/// @param <T> Entity animatable class type. This is the animatable being rendered
		/// @param <E> Entity class type. This is the entity being replaced
		/// @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
		interface CompileRenderLayers<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(Function<GeoReplacedEntityRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(GeoRenderLayer<T, E, R> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}
		}

		/// Pre-render event for armor pieces being rendered by [GeoReplacedEntityRenderer]
		///
		/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
		///
		/// Use this event to add data that you may need in a later [ReplacedEntity] event, or to override/replace data used in rendering
		///
		/// @param <T> Entity animatable class type. This is the animatable being rendered
		/// @param <E> Entity class type. This is the entity being replaced
		/// @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
		interface CompileRenderState<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
			/// Get the GeoAnimatable instance relevant to the [GeoRenderState] being compiled
			GeoAnimatable getAnimatable();

			/// Get the pre-render [net.minecraft.world.entity.Entity] that [GeoReplacedEntityRenderer] uses to build its [GeoRenderState]
			net.minecraft.world.entity.Entity getReplacedEntity();

			/// Add additional data to the [GeoRenderState]
			///
			/// @param dataTicket The DataTicket denoting the data to be added
			/// @param data The data to be added
			default <D> void addData(DataTicket<D> dataTicket, D data) {
                getRenderState().addGeckolibData(dataTicket, data);
            }
		}

		/// Pre-render event for replaced entities being rendered by [GeoReplacedEntityRenderer]
		///
		/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
		///
		/// This event is cancellable.
		/// If the event is canceled, the entity will not be rendered.
		///
		/// @param <T> Entity animatable class type. This is the animatable being rendered
		/// @param <E> Entity class type. This is the entity being replaced
		/// @param <R> RenderState class type. Typically, this would match the RenderState class the replaced entity uses in their renderer
		interface Pre<T extends GeoAnimatable, E extends net.minecraft.world.entity.Entity, R extends EntityRenderState & GeoRenderState> extends ReplacedEntity<T, E, R> {
            /// Returns the render pass info for the current render pass for this animatable
            RenderPassInfo<R> getRenderPassInfo();

            /// Get the PoseStack for the current render pass.
            /// The renderer has not yet scaled or positioned the PoseStack as this stage.
            PoseStack getPoseStack();

            /// @return The baked model for this render pass
            BakedGeoModel getModel();

            /// Returns the render submission collector for this render pass
            SubmitNodeCollector getRenderTasks();

            /// @return The camera render state for this render pass
            CameraRenderState getCameraState();
		}
	}

	/// Renderer events for [Items][ItemStack] being rendered by [GeoItemRenderer]
	///
	/// @param <T> Item animatable class type
	interface Item<T extends net.minecraft.world.item.Item & GeoAnimatable> extends GeoRenderEvent<T, GeoItemRenderer.RenderData, GeoRenderState> {
		/// Returns the renderer for this event
		@Override
		GeoItemRenderer<T> getRenderer();

		/// One-time event for a [GeoItemRenderer] called on first initialization
		///
		/// Use this event to add render layers to the renderer as needed
		///
		/// @param <T> Item animatable class type
		interface CompileRenderLayers<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			@ApiStatus.Internal
			@Override
			default GeoRenderState getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(Function<GeoItemRenderer<T>, GeoRenderLayer<T, GeoItemRenderer.RenderData, GeoRenderState>> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(GeoRenderLayer<T, GeoItemRenderer.RenderData, GeoRenderState> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}
		}

		/// Pre-render event for items being rendered by [GeoItemRenderer]
		///
		/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
		///
		/// Use this event to add data that you may need in a later [Item] event, or to override/replace data used in rendering
		///
		/// @param <T> Item animatable class type
		interface CompileRenderState<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
			/// Get the GeoAnimatable instance relevant to the [GeoRenderState] being compiled
			T getAnimatable();

			/// Get the associated render data for this render pass
			GeoItemRenderer.RenderData getRenderData();

			/// Get the pre-render ItemStack that [GeoItemRenderer] uses to build its [GeoRenderState]
			default ItemStack getItemStack() {
				return getRenderData().itemStack();
			}

			/// Get the render perspective for the render pass
			default ItemDisplayContext getRenderPerspective() {
				return getRenderData().renderPerspective();
			}

			/// Add additional data to the [GeoRenderState]
			///
			/// @param dataTicket The DataTicket denoting the data to be added
			/// @param data The data to be added
			default <D> void addData(DataTicket<D> dataTicket, D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}
		}

		/// Pre-render event for items being rendered by [GeoItemRenderer]
		///
		/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
		///
		/// This event is cancelable.
		/// If the event is canceled, the item will not be rendered.
		///
		/// @param <T> Item animatable class type
		interface Pre<T extends net.minecraft.world.item.Item & GeoAnimatable> extends Item<T> {
            /// Returns the render pass info for the current render pass for this animatable
            RenderPassInfo<GeoRenderState> getRenderPassInfo();

            /// Get the PoseStack for the current render pass.
            /// The renderer has not yet scaled or positioned the PoseStack as this stage.
            PoseStack getPoseStack();

            /// @return The baked model for this render pass
            BakedGeoModel getModel();

            /// @return The render submission collector for this render pass
            SubmitNodeCollector getRenderTasks();

            /// @return The camera render state for this render pass
            CameraRenderState getCameraState();
		}
	}

	/// Renderer events for miscellaneous [animatables][GeoAnimatable] being rendered by [GeoObjectRenderer]
	///
	/// @param <T> Object animatable class type
	/// @param <E> Associated object class type, or [Void] if none
	/// @param <R> RenderState class type
	interface Object<T extends GeoAnimatable, E, R extends GeoRenderState> extends GeoRenderEvent<T, E, R> {
		/// Returns the renderer for this event
		@Override
		GeoObjectRenderer<T, E, R> getRenderer();

		/// One-time event for a [GeoObjectRenderer] called on first initialization
		///
		/// Use this event to add render layers to the renderer as needed
		///
		/// @param <T> Object animatable class type
		/// @param <E> Associated object class type, or [Void] if none
		/// @param <R> RenderState class type
		interface CompileRenderLayers<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
			@ApiStatus.Internal
			@Override
			default R getRenderState() {
				throw new IllegalAccessError("Attempted to access render state of a CompileRenderLayers event. There is no render state for this event.");
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(Function<GeoObjectRenderer<T, E, R>, GeoRenderLayer<T, E, R>> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}

			/// Adds a [GeoRenderLayer] to the renderer
			///
			/// Type-safety is not checked here, so ensure that your layer is compatible with this animatable and renderer
			default void addLayer(GeoRenderLayer<T, E, R> renderLayer) {
				getRenderer().withRenderLayer(renderLayer);
			}
		}

		/// Pre-render event for objects being rendered by [GeoObjectRenderer]
		///
		/// This event is called in preparation for rendering, when the renderer is gathering data to pass through
		///
		/// Use this event to add data that you may need in a later [Object] event, or to override/replace data used in rendering
		///
		/// @param <T> Object animatable class type
		/// @param <E> Associated object class type, or [Void] if none
		/// @param <R> RenderState class type
		interface CompileRenderState<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
			/// Get the GeoAnimatable instance relevant to the [GeoRenderState] being compiled
			T getAnimatable();

			/// Add additional data to the [GeoRenderState]
			///
			/// @param dataTicket The DataTicket denoting the data to be added
			/// @param data The data to be added
			default <D> void addData(DataTicket<D> dataTicket, D data) {
				getRenderState().addGeckolibData(dataTicket, data);
			}

            /// Get the associated render data object for this render pass, or null if not applicable
            @Nullable E getRelatedObject();
		}

		/// Pre-render event for miscellaneous animatables being rendered by [GeoObjectRenderer]
		///
		/// This event is called before rendering, but after [GeoRenderer#preRenderPass]
		///
		/// This event is cancelable.
		/// If the event is canceled, the object will not be rendered.
		///
		/// @param <T> Object animatable class type
		/// @param <E> Associated object class type, or [Void] if none
		/// @param <R> RenderState class type
		interface Pre<T extends GeoAnimatable, E, R extends GeoRenderState> extends Object<T, E, R> {
            /// Returns the render pass info for the current render pass for this animatable
            RenderPassInfo<R> getRenderPassInfo();

            /// Get the PoseStack for the current render pass.
            /// The renderer has not yet scaled or positioned the PoseStack as this stage.
            PoseStack getPoseStack();

            /// @return The baked model for this render pass
            BakedGeoModel getModel();

            /// @return The render submission collector for this render pass
            SubmitNodeCollector getRenderTasks();

            /// @return The camera render state for this render pass
            CameraRenderState getCameraState();
		}
	}
}
