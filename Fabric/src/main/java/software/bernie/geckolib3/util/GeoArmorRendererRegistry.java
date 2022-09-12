package software.bernie.geckolib3.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;

public interface GeoArmorRendererRegistry {

	GeoArmorRendererRegistry INSTANCE = new GeoArmorRegistryImpl();

	<E extends Entity> void register(Class<? extends ArmorItem> itemClass,
			GeoArmorRendererFactory<E> entityRendererFactory);
}
