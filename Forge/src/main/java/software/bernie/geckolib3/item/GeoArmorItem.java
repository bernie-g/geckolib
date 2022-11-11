/*
package software.bernie.geckolib3.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

*/
/**
 * Wrapper class for GeckoLib armor items.<br>
 * Not specifically required to be used, but it does do some required work you will need to replicate if you don't use it.
 *//*

public abstract class GeoArmorItem extends ArmorItem {
	public GeoArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
					EquipmentSlot slot, HumanoidModel<?> defaultModel) {
				return (HumanoidModel<?>)GeoArmorRenderer.getRenderer(GeoArmorItem.this.getClass(), entity)
						.applyEntityStats(defaultModel)
						.setCurrentItem(entity, stack, slot)
						.applySlot(slot);
			}
		});
	}

	@Nullable
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		Class<? extends ArmorItem> clazz = this.getClass();
		GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz, entity);
		return renderer.getTextureLocation((ArmorItem)stack.getItem()).toString();
	}
}
*/
