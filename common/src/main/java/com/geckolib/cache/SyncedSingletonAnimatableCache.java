package com.geckolib.cache;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import com.geckolib.GeckoLibConstants;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.SingletonGeoAnimatable;

import java.util.Map;

/// Caching class for [SingletonGeoAnimatable]s that have been registered as syncable
public final class SyncedSingletonAnimatableCache {
    private static final Int2ObjectMap<String> ANIMATABLE_IDENTITIES = new Int2ObjectOpenHashMap<>();
    private static final Map<String, GeoAnimatable> SYNCED_ANIMATABLES = new Object2ObjectOpenHashMap<>();

    /// Registers a synced [SingletonGeoAnimatable] object for networking support
    ///
    /// It is recommended that you don't call this directly, instead implementing and calling [SingletonGeoAnimatable#registerSyncedAnimatable]
    @ApiStatus.Internal
    public static void registerSyncedAnimatable(SingletonGeoAnimatable animatable) {
        synchronized (SYNCED_ANIMATABLES) {
            GeoAnimatable existing = SYNCED_ANIMATABLES.put(getOrCreateId(animatable), animatable);

            if (existing == null)
                GeckoLibConstants.LOGGER.debug("Registered SyncedAnimatable for {}", animatable.getClass());
        }
    }

    /// Gets a registered synced [SingletonGeoAnimatable] object by name
    ///
    /// @param syncedAnimatableId the className
    @ApiStatus.Internal
    public static @Nullable GeoAnimatable getSyncedAnimatable(String syncedAnimatableId) {
        GeoAnimatable animatable = SYNCED_ANIMATABLES.get(syncedAnimatableId);

        if (animatable == null)
            GeckoLibConstants.LOGGER.error("Attempting to retrieve unregistered synced animatable! ({})", syncedAnimatableId);

        return animatable;
    }

    /// Get a synced singleton animatable's id for use with [#SYNCED_ANIMATABLES]
    ///
    /// This **<u>MUST</u>** be used when retrieving from [#SYNCED_ANIMATABLES]
    /// as this method eliminates class duplication collisions
    public static String getOrCreateId(SingletonGeoAnimatable animatable) {
        return ANIMATABLE_IDENTITIES.computeIfAbsent(System.identityHashCode(animatable), i -> {
            String baseId = animatable.getClass().getName();
            i = 0;

            while (SYNCED_ANIMATABLES.containsKey(baseId + i)) {
                i++;
            }

            return baseId + i;
        });
    }
}
