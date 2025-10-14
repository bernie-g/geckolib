package software.bernie.geckolib.renderer.base;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

/**
 * Duck interface injected into all RenderState-like objects that are relevant for GeckoLib rendering
 * <p>
 * This allows for GeckoLib to inject DataTicket-linked data easily to RenderStates.
 * <p>
 * This class should be safely castable to the RenderState for your renderer (E.G. {@link EntityRenderState} for {@link GeoEntityRenderer})
 */
public interface GeoRenderState {
    /**
     * Add data to the RenderState
     * @param dataTicket The DataTicket identifying the data
     * @param data The associated data
     */
    <D> void addGeckolibData(DataTicket<D> dataTicket, @Nullable D data);

    /**
     * @return Whether the RenderState has data associated with the given {@link DataTicket}
     */
    boolean hasGeckolibData(DataTicket<?> dataTicket);

    /**
     * Get previously set data on the RenderState by its associated {@link DataTicket}.
     * <p>
     * Note that you should <b><u>NOT</u></b> be attempting to retrieve data you don't know exists.<br>
     * Use {@link #hasGeckolibData(DataTicket)} if unsure
     *
     * @param dataTicket The DataTicket associated with the data
     * @return The data contained on this RenderState, null if the data is set to null, or an exception if the data doesn't exist
     */
    @Nullable
    <D> D getGeckolibData(DataTicket<D> dataTicket);

    /**
     * Get previously set data on the RenderState by its associated {@link DataTicket},
     * or a default value if the data does not exist
     *
     * @param dataTicket The DataTicket associated with the data
     * @param defaultValue The fallback value if no data has been set for the given DataTicket
     * @return The data contained on this RenderState, null if the data is set to null, or {@code defaultValue} if not present
     */
    @Nullable
    default <D> D getOrDefaultGeckolibData(DataTicket<D> dataTicket, @Nullable D defaultValue) {
        D data = getGeckolibData(dataTicket);

        return data != null || hasGeckolibData(dataTicket) ? data : defaultValue;
    }

    /**
     * Helper method for returning the 'packed' light coordinates value for this render pass
     * <p>
     * Some RenderState implementations contain this as a field, so this method allows for capturing that
     *
     * @return The packed int light coordinates
     */
    default int getPackedLight() {
        return getOrDefaultGeckolibData(DataTickets.PACKED_LIGHT, LightTexture.FULL_BRIGHT);
    }

    /**
     * Helper method for returning the fraction of a tick that has passed since the last tick, as of this render pass.
     *
     * @return The partialTick value
     */
    default float getPartialTick() {
        return getOrDefaultGeckolibData(DataTickets.PARTIAL_TICK, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    /**
     * Internal method used for more complex handling internally.
     * <p>
     * <b>YOU SHOULD NOT BE USING THIS</b>
     */
    @ApiStatus.Internal
    Map<DataTicket<?>, Object> getDataMap();

    /**
     * Built-in implementation class for GeoRenderState, used for Geo renderers that don't already have one
     */
    @ApiStatus.Internal
    record Impl(Map<DataTicket<?>, Object> data) implements GeoRenderState {
        public Impl() {
            this(new Reference2ObjectOpenHashMap<>());
        }

        @Override
        public <D> void addGeckolibData(DataTicket<D> dataTicket, @Nullable D data) {
            this.data.put(dataTicket, data);
        }

        @Override
        public boolean hasGeckolibData(DataTicket<?> dataTicket) {
            return this.data.containsKey(dataTicket);
        }

        @Nullable
        @Override
        public <D> D getGeckolibData(DataTicket<D> dataTicket) {
            Object data = this.data.get(dataTicket);

            if (data == null && !hasGeckolibData(dataTicket))
                throw new IllegalArgumentException("Attempted to retrieve data from GeoRenderState that does not exist. Check your code!");

            try {
                return (D)data;
            }
            catch (ClassCastException ex) {
                GeckoLibConstants.LOGGER.error("Attempted to retrieve incorrectly typed data from GeoRenderState. Possibly a mod or DataTicket conflict? Expected: {}, found data type {}", dataTicket, data.getClass().getName(), ex);

                throw ex;
            }
        }

        @Nullable
        @Override
        public <D> D getOrDefaultGeckolibData(DataTicket<D> dataTicket, @Nullable D defaultValue) {
            Object data = this.data.get(dataTicket);

            if (data == null && !hasGeckolibData(dataTicket))
                return defaultValue;

            try {
                return (D)data;
            }
            catch (ClassCastException ex) {
                GeckoLibConstants.LOGGER.error("Attempted to retrieve incorrectly typed data from GeoRenderState. Possibly a mod or DataTicket conflict? Expected: {}, found data type {}", dataTicket, data.getClass().getName(), ex);

                return defaultValue;
            }
        }

        @ApiStatus.Internal
        @Override
        public Map<DataTicket<?>, Object> getDataMap() {
            return this.data;
        }
    }
}
