package software.bernie.example.item;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;
import software.bernie.geckolib.item.GeoArmorItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotatoArmorItem extends GeoArmorItem implements IAnimatable
{
	private AnimationFactory factory = new AnimationFactory(this);

	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event)
	{
		List<EquipmentSlotType> slotData = event.getExtraDataOfType(EquipmentSlotType.class);
		List<ItemStack> stackData = event.getExtraDataOfType(ItemStack.class);
		LivingEntity entityData = event.getExtraDataOfType(LivingEntity.class).get(0);
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.potato_armor.new", true));
		if (entityData instanceof ArmorStandEntity)
		{
			return PlayState.CONTINUE;
		}
		else if (entityData instanceof ClientPlayerEntity)
		{
			ClientPlayerEntity client = (ClientPlayerEntity) entityData;
			List<Item> equipmentList = new ArrayList<>();
			client.getEquipmentAndArmor().forEach((x) -> equipmentList.add(x.getItem()));
			boolean isWearingAll = equipmentList.containsAll(Arrays.asList(ItemRegistry.POTATO_BOOTS.get(), ItemRegistry.POTATO_LEGGINGS.get(), ItemRegistry.POTATO_CHEST.get(), ItemRegistry.POTATO_HEAD.get()));
			return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
		}
		return PlayState.STOP;
	}

	public PotatoArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder)
	{
		super(materialIn, slot, builder);
	}

	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController(this, "controller", 20, this::predicate));
	}

	@Override
	public AnimationFactory getFactory()
	{
		return this.factory;
	}
}
