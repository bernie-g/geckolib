package software.bernie.geckolib3.item;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public abstract class GeoArmorItem extends ArmorItem {
	public GeoArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			@Override
			public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
					EquipmentSlot armorSlot, HumanoidModel<?> _default) {
				return (HumanoidModel<?>) GeoArmorRenderer.getRenderer(GeoArmorItem.this.getClass(), entityLiving)
						.applyEntityStats(_default).setCurrentItem(entityLiving, itemStack, armorSlot)
						.applySlot(armorSlot);
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
