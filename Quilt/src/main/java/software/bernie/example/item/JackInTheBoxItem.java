package software.bernie.example.item;

import org.quiltmc.qsl.networking.api.PlayerLookup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3q.network.GeckoLibNetwork;
import software.bernie.geckolib3q.network.ISyncable;
import software.bernie.geckolib3q.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements IAnimatable, ISyncable {
	public AnimationFactory factory = new AnimationFactory(this);
	private static final String controllerName = "popupController";
	private static final int ANIM_OPEN = 0;

	public JackInTheBoxItem(Properties properties) {
		super(properties);
		GeckoLibNetwork.registerSyncable(this);
	}

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<JackInTheBoxItem> controller = new AnimationController(this, controllerName, 20,
				this::predicate);

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
		LocalPlayer player = Minecraft.getInstance().player;
		player.playSound(SoundRegistry.JACK_MUSIC, 1, 1);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		if (!world.isClientSide()) {
			final int id = GeckoLibUtil.guaranteeIDForStack(user.getItemInHand(hand), (ServerLevel) world);
			GeckoLibNetwork.syncAnimation(user, this, id, ANIM_OPEN);
			for (Player otherPlayer : PlayerLookup.tracking(user)) {
				GeckoLibNetwork.syncAnimation(otherPlayer, this, id, ANIM_OPEN);
			}
		}
		return super.use(world, user, hand);
	}

	@Override
	public void onAnimationSync(int id, int state) {
		if (state == ANIM_OPEN) {
			// Always use GeckoLibUtil to get AnimationControllers when you don't have
			// access to an AnimationEvent
			final AnimationController controller = GeckoLibUtil.getControllerForID(this.factory, id, controllerName);

			if (controller.getAnimationState() == AnimationState.Stopped) {
				final LocalPlayer player = Minecraft.getInstance().player;
				if (player != null) {
					player.displayClientMessage(new TextComponent("Opening the jack in the box!"), true);
				}
				// If you don't do this, the popup animation will only play once because the
				// animation will be cached.
				controller.markNeedsReload();
				// Set the animation to open the JackInTheBoxItem which will start playing music
				// and
				// eventually do the actual animation. Also sets it to not loop
				controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", EDefaultLoopTypes.PLAY_ONCE));
			}
		}
	}
}