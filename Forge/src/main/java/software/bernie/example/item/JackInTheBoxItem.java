package software.bernie.example.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.animatable.GeoItem;
import software.bernie.geckolib3.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.RawAnimation;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.ClientUtils;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * Example {@link GeoItem} implementation in the form of a Jack-in-the-Box.<br>
 */
public final class JackInTheBoxItem extends Item implements GeoItem {
	private static final RawAnimation POPUP_ANIM = RawAnimation.begin().thenPlay("use.popup");
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public JackInTheBoxItem(Properties properties) {
		super(properties.tab(GeckoLibMod.ITEM_GROUP));

		// Register our item as server-side handled.
		// This enables both animation data syncing and server-side animation triggering
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	// Utilise the existing forge hook to define our custom renderer (which we created in createRenderer)
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private JackInTheBoxRenderer renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new JackInTheBoxRenderer();

				return this.renderer;
			}
		});
	}

	// Let's add our animation controller
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addAnimationController(new AnimationController<>(this, "popup_controller", 20, event -> PlayState.STOP)
				.triggerableAnim("box_open", POPUP_ANIM)
				// We've marked the "box_open" animation as being triggerable from the server
				.setSoundKeyframeHandler(event -> {
					// Use helper method to avoid client-code in common class
					Player player = ClientUtils.getClientPlayer();

					if (player != null)
						player.playSound(SoundRegistry.JACK_MUSIC.get(), 1, 1);
				}));
	}

	// Let's handle our use method so that we activate the animation when right-clicking while holding the box
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level instanceof ServerLevel serverLevel)
			triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), serverLevel), "popup_controller", "box_open");

		return super.use(level, player, hand);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}
}
