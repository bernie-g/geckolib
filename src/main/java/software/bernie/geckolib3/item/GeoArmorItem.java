package software.bernie.geckolib3.item;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public abstract class GeoArmorItem extends ArmorItem {
	public GeoArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {

			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
					EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				return (HumanoidModel<?>) GeoArmorRenderer.getRenderer(GeoArmorItem.this.getClass(), livingEntity)
						.applyEntityStats(original).setCurrentItem(livingEntity, itemStack, equipmentSlot)
						.applySlot(equipmentSlot);
			}
		});
	}

	@Nullable
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		Class<? extends ArmorItem> clazz = this.getClass();
		GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz, entity);
		return renderer.getTextureLocation((ArmorItem) stack.getItem()).toString();
	}
}
