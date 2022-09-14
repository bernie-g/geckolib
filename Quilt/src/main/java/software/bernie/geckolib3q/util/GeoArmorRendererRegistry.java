package software.bernie.geckolib3q.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;

public interface GeoArmorRendererRegistry {

	GeoArmorRendererRegistry INSTANCE = new GeoArmorRegistryImpl();

	<E extends Entity> void register(Class<? extends ArmorItem> itemClass,
			GeoArmorRendererFactory<E> entityRendererFactory);
}
