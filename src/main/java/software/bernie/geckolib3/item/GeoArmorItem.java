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

			@SuppressWarnings("unchecked")
			@Override
			public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
					EquipmentSlot armorSlot, A _default) {
				return (A) GeoArmorRenderer.getRenderer(GeoArmorItem.this.getClass()).applyEntityStats(_default)
						.applySlot(armorSlot).setCurrentItem(entityLiving, itemStack, armorSlot);
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Nullable
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		Class<? extends ArmorItem> clazz = this.getClass();
		GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz);
		return renderer.getTextureLocation((ArmorItem) stack.getItem()).toString();
	}
}
