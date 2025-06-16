package anightdazingzoroark.example.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import anightdazingzoroark.example.RiftLibMod;
import anightdazingzoroark.example.registry.ItemRegistry;
import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.PlayState;
import anightdazingzoroark.riftlib.core.builder.AnimationBuilder;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;
import anightdazingzoroark.riftlib.item.GeoArmorItem;

//This is an example of animated armor. Make sure to read the comments thoroughly and also check out PotatoArmorRenderer.
public class PotatoArmorItem extends GeoArmorItem implements IAnimatable {
	private AnimationFactory factory = new AnimationFactory(this);

	public PotatoArmorItem(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot slot) {
		super(materialIn, renderIndexIn, slot);

		this.setCreativeTab(RiftLibMod.getRiftlibItemGroup());
	}

	// Predicate runs every frame
	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		// This is all the extradata this event carries. The livingentity is the entity
		// that's wearing the armor. The itemstack and equipmentslottype are self
		// explanatory.
		EntityLivingBase livingEntity = event.getExtraDataOfType(EntityLivingBase.class).get(0);

		// Always loop the animation but later on in this method we'll decide whether or
		// not to actually play it
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.potato_armor.new", true));

		// If the living entity is an armorstand just play the animation nonstop
		if (livingEntity instanceof EntityArmorStand) {
			return PlayState.CONTINUE;
		}

		// The entity is a player, so we want to only play if the player is wearing the
		// full set of armor
		else if (livingEntity instanceof EntityPlayerSP) {
			EntityPlayerSP client = (EntityPlayerSP) livingEntity;

			// Get all the equipment, aka the armor, currently held item, and offhand item
			List<Item> equipmentList = new ArrayList<>();
			client.getEquipmentAndArmor().forEach((x) -> equipmentList.add(x.getItem()));

			// elements 2 to 6 are the armor so we take the sublist. Armorlist now only
			// contains the 4 armor slots
			List<Item> armorList = equipmentList.subList(2, 6);

			// Make sure the player is wearing all the armor. If they are, continue playing
			// the animation, otherwise stop
			boolean isWearingAll = armorList.containsAll(Arrays.asList(ItemRegistry.POTATO_BOOTS,
					ItemRegistry.POTATO_LEGGINGS, ItemRegistry.POTATO_CHEST, ItemRegistry.POTATO_HEAD));
			return isWearingAll ? PlayState.CONTINUE : PlayState.STOP;
		}
		return PlayState.STOP;
	}

	// All you need to do here is add your animation controllers to the
	// AnimationData
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<PotatoArmorItem>(this, "controller", 20, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
