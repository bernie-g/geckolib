package software.bernie.geckolib3.item;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import javax.annotation.Nullable;

public abstract class GeoArmorItem extends ArmorItem {
	public GeoArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
			EquipmentSlotType armorSlot, A _default) {
		Class<? extends ArmorItem> clazz = this.getClass();
		GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz);
		renderer.applyEntityStats(_default).applySlot(armorSlot);
		renderer.setCurrentItem(entityLiving, itemStack, armorSlot);
		return (A) renderer;
	}

	@Nullable
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		Class<? extends ArmorItem> clazz = this.getClass();
		GeoArmorRenderer renderer = GeoArmorRenderer.getRenderer(clazz);
		return renderer.getTextureLocation((ArmorItem) stack.getItem()).toString();
	}
}
