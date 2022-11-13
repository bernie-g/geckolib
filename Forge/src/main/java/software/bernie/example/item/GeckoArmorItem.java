package software.bernie.example.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.renderer.armor.GeckoArmorRenderer;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.animatable.GeoArmor;
import software.bernie.geckolib3.constant.DataTickets;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Example {@link GeoArmor} implementation.<br>
 * @see GeoArmorItem
 * @see GeoArmor
 * @see GeckoArmorRenderer
 */
public final class GeckoArmorItem extends GeoArmorItem {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public GeckoArmorItem(ArmorMaterial armorMaterial, EquipmentSlot slot, Properties properties) {
		super(armorMaterial, slot, properties.tab(GeckoLibMod.ITEM_GROUP));
	}

	// Let's return our armor renderer for this item.
	// This is for the in-world rendering (when equipped)
	@Override
	public void createRenderer(Consumer<RenderProvider> consumer) {
		consumer.accept(new RenderProvider() {
			private final GeoArmorRenderer<?> renderer = new GeckoArmorRenderer();

			@Override
			public GeoArmorRenderer<?> getArmorRenderer(GeoArmor armor) {
				return renderer;
			}
		});
	}

	// Let's add our animation controller
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addAnimationController(new AnimationController<>(this, 20, event -> {
			// Apply our generic idle animation.
			// Whether it plays or not is decided down below.
			event.getController().setAnimation(DefaultAnimations.IDLE);

			// Let's gather some data from the event to use below
			// This is the entity that is currently wearing/holding the item
			Entity entity = event.getData(DataTickets.ENTITY);

			// We'll just have ArmorStands always animate, so we can return here
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
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}