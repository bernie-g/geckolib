package software.bernie.example.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.renderer.armor.WolfArmorRenderer;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.animatable.GeoItem;
import software.bernie.geckolib3.constant.DataTickets;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Example {@link software.bernie.geckolib3.core.animatable.GeoAnimatable GeoAnimatable} {@link ArmorItem} implementation
 * @see GeoItem
 * @see WolfArmorRenderer
 */
public final class WolfArmorItem extends ArmorItem implements GeoItem {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public WolfArmorItem(ArmorMaterial armorMaterial, EquipmentSlot slot, Properties properties) {
		super(armorMaterial, slot, properties.tab(GeckoLibMod.ITEM_GROUP));
	}

	// Create our armor model/renderer for forge and return it
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
				if (this.renderer == null)
					this.renderer = new WolfArmorRenderer();

				// This prepares our GeoArmorRenderer for the current render frame.
				// These parameters may be null however, so we don't do anything further with them
				this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

				return this.renderer;
			}
		});
	}

	// Let's add our animation controller
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addController(new AnimationController<>(this, 20, event -> {
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
					ItemRegistry.WOLF_ARMOR_BOOTS.get(),
					ItemRegistry.WOLF_ARMOR_LEGGINGS.get(),
					ItemRegistry.WOLF_ARMOR_CHESTPLATE.get(),
					ItemRegistry.WOLF_ARMOR_HELMET.get()));

			// Play the animation if the full set is being worn, otherwise stop
			return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
		}));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}