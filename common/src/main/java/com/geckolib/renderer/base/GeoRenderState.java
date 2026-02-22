package com.geckolib.renderer.base;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.LightCoordsUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;
import java.util.function.Supplier;

/// Duck interface injected into all RenderState-like objects that are relevant for GeckoLib rendering
///
/// This allows for GeckoLib to inject DataTicket-linked data easily to RenderStates.
///
/// This class should be safely castable to the RenderState for your renderer (E.G. [EntityRenderState] for [GeoEntityRenderer])
public interface GeoRenderState {
    /// Add data to the RenderState
    ///
    /// @param dataTicket The DataTicket identifying the data
    /// @param data The associated data
    default <D> void addGeckolibData(DataTicket<D> dataTicket, D data) {
        getDataMap().put(dataTicket, data);
    }

    /// @return Whether the RenderState has data associated with the given [DataTicket]
    default boolean hasGeckolibData(DataTicket<?> dataTicket) {
        return getDataMap().containsKey(dataTicket);
    }

    /// Get previously set data on the RenderState by its associated [DataTicket].
    ///
    /// @param dataTicket The DataTicket associated with the data
    /// @return The data contained on this RenderState, null if the data doesn't exist
    default <D> @Nullable D getGeckolibData(DataTicket<D> dataTicket) {
        Object data = getDataMap().get(dataTicket);

        try {
            //noinspection unchecked
            return (D)data;
        }
        catch (ClassCastException ex) {
            GeckoLibConstants.LOGGER.error("Attempted to retrieve incorrectly typed data from GeoRenderState. Possibly a mod or DataTicket conflict? Expected: {}, found data type {}", dataTicket, data.getClass().getName(), ex);

            throw ex;
        }
    }

    /// Get previously set data on the RenderState by its associated [DataTicket],
    /// or a default value if the data does not exist
    ///
    /// @param dataTicket The DataTicket associated with the data
    /// @param defaultValue The fallback value if no data has been set for the given DataTicket
    /// @return The data contained on this RenderState, or `defaultValue` if not present
    @Contract("_,null->null;_,!null->!null")
    default <D> @Nullable D getOrDefaultGeckolibData(DataTicket<D> dataTicket, @Nullable D defaultValue) {
        D data = getGeckolibData(dataTicket);

        return data != null ? data : defaultValue;
    }

    /// Get previously set data on the RenderState by its associated [DataTicket],
    /// or a default value if the data does not exist
    ///
    /// @param dataTicket The DataTicket associated with the data
    /// @param defaultValue A supplier for the fallback value if no data has been set for the given DataTicket
    /// @return The data contained on this RenderState, or `defaultValue` if not present
    default <D> @Nullable D getOrDefaultGeckolibData(DataTicket<D> dataTicket, Supplier<@Nullable D> defaultValue) {
        D data = getGeckolibData(dataTicket);

        return data != null ? data : defaultValue.get();
    }

    /// Helper method for returning the 'packed' light coordinates value for this render pass
    ///
    /// Some RenderState implementations contain this as a field, so this method allows for capturing that
    ///
    /// @return The packed int light coordinates
    default int getPackedLight() {
        return getOrDefaultGeckolibData(DataTickets.PACKED_LIGHT, LightCoordsUtil.FULL_BRIGHT);
    }

    /// Helper method for returning the fraction of a tick that has passed since the last tick, as of this render pass.
    ///
    /// @return The partialTick value
    default float getPartialTick() {
        return getOrDefaultGeckolibData(DataTickets.PARTIAL_TICK, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    /// Get the age of the animatable in ticks, including the current partial tick
    default double getAnimatableAge() {
        return getOrDefaultGeckolibData(DataTickets.TICK, 0d);
    }

    /// Internal method used for more complex handling internally.
    ///
    /// **YOU SHOULD NOT BE USING THIS**
    @ApiStatus.Internal
    Map<DataTicket<?>, Object> getDataMap();

    /// Built-in implementation class for GeoRenderState, used for Geo renderers that don't already have one
    ///
    /// @param data The internal ticket-data map for this RenderState instance
    @ApiStatus.Internal
    record Impl(Map<DataTicket<?>, Object> data) implements GeoRenderState {
        public Impl() {
            this(new Reference2ObjectOpenHashMap<>());
        }

        @ApiStatus.Internal
        @Override
        public Map<DataTicket<?>, Object> getDataMap() {
            return this.data;
        }
    }
}
