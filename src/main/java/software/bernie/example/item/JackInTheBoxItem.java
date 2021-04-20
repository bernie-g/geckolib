package software.bernie.example.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.IAnimatableItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements IAnimatableItem {
	private static final String CONTROLLER_NAME = "popupController";
	private AnimationFactory factory = new AnimationFactory(this);

	public JackInTheBoxItem(Properties properties) {
		super(properties.tab(GeckoLibMod.geckolibItemGroup));
	}

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		// Not setting an animation here as that's handled below
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController controller = new AnimationController(this, CONTROLLER_NAME, 20, this::predicate);

		// Registering a sound listener just makes it so when any sound keyframe is hit
		// the method will be called.
		// To register a particle listener or custom event listener you do the exact
		// same thing, just with registerParticleListener and
		// registerCustomInstructionListener, respectively.
		controller.registerSoundListener(this::soundListener);
		data.addAnimationController(controller);
	}

	private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
		// The animation for the jackinthebox has a sound keyframe at time 0:00.
		// As soon as that keyframe gets hit this method fires and it starts playing the
		// sound to the current player.
		// The music is synced with the animation so the box opens as soon as the music
		// plays the box opening sound
		ClientPlayerEntity player = Minecraft.getInstance().player;
		player.playSound(SoundRegistry.JACK_MUSIC.get(), 1, 1);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide) {
			GeckoLibUtil.ensureIdExists(this, stack, (ServerWorld) world);
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand) {
		if (worldIn.isClientSide) {
			// Gets the item that the player is holding, should be a JackInTheBox
			ItemStack stack = player.getItemInHand(hand);

			// Always use GeckoLibUtil to get animationcontrollers when you don't have
			// access to an AnimationEvent
			AnimationController controller = GeckoLibUtil.getController(this.factory, this.getId(stack), CONTROLLER_NAME);

			if (controller.getAnimationState() == AnimationState.Stopped) {
				player.displayClientMessage(new StringTextComponent("Opening the jack in the box!"), true);
				// If you don't do this, the popup animation will only play once because the
				// animation will be cached.
				controller.markNeedsReload();
				// Set the animation to open the jackinthebox which will start playing music and
				// eventually do the actual animation. Also sets it to not loop
				controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", false));
			}
		}
		return super.use(worldIn, player, hand);
	}
}
