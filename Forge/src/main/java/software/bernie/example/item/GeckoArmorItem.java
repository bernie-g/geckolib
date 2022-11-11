/*
package software.bernie.example.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.animatable.GeoItem;
import software.bernie.geckolib3.constant.DataTickets;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimationData;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Set;

*/
/**
 * Example {@link GeoItem} implementation in the form of a wearable armor.<br>
 * @see GeoArmorItem
 *//*

public final class GeckoArmorItem extends GeoArmorItem implements GeoItem {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public GeckoArmorItem(ArmorMaterial armorMaterial, EquipmentSlot slot, Properties properties) {
		super(armorMaterial, slot, properties.tab(GeckoLibMod.ITEM_GROUP));
	}

	*/
/**
	 * Register the animation controller for this item
	 *//*

	@Override
	public void registerControllers(AnimationData<?> data) {
		data.addAnimationController(new AnimationController<>(this, 20, this::checkAnimations));
	}

	*/
/**
	 * Simple getter to return the animation factory for this item
	 *//*

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	*/
/**
	 * Our animation predicate for this item. It has been split off into this separate method for easy reading.
	 *//*

	private <A extends GeckoArmorItem> PlayState checkAnimations(AnimationEvent<A> event) {
		// Apply our generic idle animation.
		// Whether it plays or not is decided down below.
		event.getController().setAnimation(DefaultAnimations.IDLE);

		// Let's gather some data from the event to use below
		// This is the entity that is currently wearing/holding the item
		LivingEntity entity = event.getData(DataTickets.LIVING_ENTITY);

		// We'll just have armorstands always animate, so we can return here
		if (entity instanceof ArmorStand)
			return PlayState.CONTINUE;

		// For this example, we only want the animation to play if the entity is wearing all pieces of the armor
		// Let's collect the armor pieces the entity is currently wearing
		Set<Item> wornArmor = new ObjectOpenHashSet<>();

		for (ItemStack stack : entity.getArmorSlots()) {
			// We can stop immediately if any of the slots are empty
			if (stack.isEmpty())
				return PlayState.STOP;

			wornArmor.add(stack.getItem());
		}

		// Check each of the pieces match our set
		boolean isFullSet = wornArmor.containsAll(ObjectArrayList.of(
				ItemRegistry.GECKOARMOR_BOOTS.get(),
				ItemRegistry.GECKOARMOR_LEGGINGS.get(),
				ItemRegistry.GECKOARMOR_CHEST.get(),
				ItemRegistry.GECKOARMOR_HEAD.get()));

		// Play the animation if the full set is being worn, otherwise stop
		return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
	}
}
*/
