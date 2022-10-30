package software.bernie.example.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class GeckoArmorItem extends GeoArmorItem implements IAnimatable {
	public AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public GeckoArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
		super(materialIn, slot, builder.tab(GeckoLibMod.geckolibItemGroup));
	}

	// Predicate runs every frame
	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		// This is all the extradata this event carries. The livingentity is the entity
		// that's wearing the armor. The itemstack and equipmentslottype are self
		// explanatory.
		LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);

		// Always loop the animation but later on in this method we'll decide whether or
		// not to actually play it
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.potato_armor.new", EDefaultLoopTypes.LOOP));

		// If the living entity is an armorstand just play the animation nonstop
		if (livingEntity instanceof ArmorStandEntity) {
			return PlayState.CONTINUE;
		}


		// elements 2 to 6 are the armor so we take the sublist. Armorlist now only
		// contains the 4 armor slots
		List<Item> armorList = new ArrayList<>(4);
		for(EquipmentSlotType slot : EquipmentSlotType.values()) {
			if(slot.getType() == Group.ARMOR) {
				if(livingEntity.getItemBySlot(slot) != null) {
					armorList.add(livingEntity.getItemBySlot(slot).getItem());
				}
			}
		}

		// Make sure the player is wearing all the armor. If they are, continue playing
		// the animation, otherwise stop
		boolean isWearingAll = armorList
				.containsAll(Arrays.asList(ItemRegistry.GECKOARMOR_BOOTS.get(), ItemRegistry.GECKOARMOR_LEGGINGS.get(),
						ItemRegistry.GECKOARMOR_CHEST.get(), ItemRegistry.GECKOARMOR_HEAD.get()));
		return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
	}

	// All you need to do here is add your animation controllers to the
	// AnimationData
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<GeckoArmorItem>(this, "controller", 20, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
