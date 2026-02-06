package software.bernie.geckolib.animatable.instance;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.DataTicket;

import java.util.function.Supplier;

/// The base cache class responsible for returning the [AnimatableManager] for a given instanceof of a [GeoAnimatable]
///
/// This class is abstracted and not intended for direct use.<br/>
/// See either [SingletonAnimatableInstanceCache] or [InstancedAnimatableInstanceCache]
public abstract class AnimatableInstanceCache {
	protected final GeoAnimatable animatable;
	protected final Supplier<GeoRenderProvider> renderProvider;

	public AnimatableInstanceCache(GeoAnimatable animatable) {
		this.animatable = animatable;
		this.renderProvider = Suppliers.memoize(() -> {
			if (!(this.animatable instanceof SingletonGeoAnimatable singleton) || !GeckoLibServices.PLATFORM.isPhysicalClient())
				return null;

			final MutableObject<GeoRenderProvider> consumer = new MutableObject<>(GeoRenderProvider.DEFAULT);

			singleton.createGeoRenderer(consumer::setValue);

			return consumer.get();
		});
	}

	/// This creates or gets the cached animatable manager for any unique ID
	///
	/// For [ItemStack]s, this is typically a reserved ID provided by GeckoLib. `Entities` and `BlockEntities`
	/// pass their position or int ID. They typically only have one [AnimatableManager] per cache anyway
	///
	/// @param uniqueId A unique ID. For every ID the same animation manager
	///                 will be returned.
	public abstract <T extends GeoAnimatable> AnimatableManager<T> getManagerForId(long uniqueId);

	/// Helper method to set a data point in the [manager][AnimatableManager#setAnimatableData] for this animatable
	///
	/// @param uniqueId The unique identifier for this animatable instance
	/// @param dataTicket The DataTicket for the data
	/// @param data The data to store
	public <D> void addDataPoint(long uniqueId, DataTicket<D> dataTicket, D data) {
		getManagerForId(uniqueId).setAnimatableData(dataTicket, data);
	}

	/// Helper method to get a data point from the [data collection][AnimatableManager#getAnimatableData] for this animatable,
	/// or null if the data isn't present
	///
	/// @param uniqueId The unique identifier for this animatable instance
	/// @param dataTicket The DataTicket for the data
	public <D> @Nullable D getDataPoint(long uniqueId, DataTicket<D> dataTicket) {
		return getManagerForId(uniqueId).getAnimatableData(dataTicket);
	}

	/// Get the [GeoRenderProvider] for this animatable
	///
	/// Because only [SingletonGeoAnimatable]s use this functionality, this method should not be used for anything other than a SingletonGeoAnimatable
	///
	/// The returned object is upcast to Object for side-safety
	///
	/// @return The cached GeoRenderProvider instance for this animatable
	public Object getRenderProvider() {
		return this.renderProvider.get();
	}
}
