package software.bernie.example.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

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
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements IAnimatable, ISyncable {
	private static final String CONTROLLER_NAME = "popupController";
	private static final int ANIM_OPEN = 0;
	public AnimationFactory factory = new AnimationFactory(this);

	public JackInTheBoxItem(Properties properties) {
		super(properties.tab(GeckoLibMod.geckolibItemGroup));
		GeckoLibNetwork.registerSyncable(this);
	}

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		// Not setting an animation here as that's handled in onItemRightClick
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
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (!world.isClientSide) {
			// Gets the item that the player is holding, should be a JackInTheBoxItem
			final ItemStack stack = player.getItemInHand(hand);
			final int id = GeckoLibUtil.getIDFromStack(stack);
			// Tell all nearby clients to trigger this JackInTheBoxItem
			final PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
			GeckoLibNetwork.syncAnimation(target, this, id, ANIM_OPEN);
		}
		return super.use(world, player, hand);
	}

	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ANIM_OPEN) {
			// Always use GeckoLibUtil to get AnimationControllers when you don't have
			// access to an AnimationEvent
			final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, CONTROLLER_NAME);

			if (controller.getAnimationState() == AnimationState.Stopped) {
				final ClientPlayerEntity player = Minecraft.getInstance().player;
				if (player != null) {
					player.displayClientMessage(new StringTextComponent("Opening the jack in the box!"), true);
				}
				// If you don't do this, the popup animation will only play once because the
				// animation will be cached.
				controller.markNeedsReload();
				// Set the animation to open the JackInTheBoxItem which will start playing music and
				// eventually do the actual animation. Also sets it to not loop
				controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", false));
			}
		}
	}
}
