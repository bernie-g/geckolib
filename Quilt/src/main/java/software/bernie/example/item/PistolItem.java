package software.bernie.example.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import software.bernie.example.client.renderer.item.PistolRenderer;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Example {@link GeoItem} in the form of a "shootable" pistol.
 * @see PistolRenderer
 */
public class PistolItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	public PistolItem() {
		super(new Properties().stacksTo(1).durability(201));

		// Register our item as server-side handled.
		// This enables both animation data syncing and server-side animation triggering
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	// Utilise our own render hook to define our custom renderer
	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(new RenderProvider() {
			private final PistolRenderer renderer = new PistolRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return this.renderer;
			}
		});
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return this.renderProvider;
	}

	// Register our animation controllers
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addController(new AnimationController<>(this, "shoot_controller", event -> PlayState.CONTINUE)
				.triggerableAnim("shoot", DefaultAnimations.ITEM_ON_USE));
		// We've marked the "shoot" animation as being triggerable from the server
	}

	// Start "using" the item once clicked
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		player.startUsingItem(hand);

		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	// Fire an arrow and play the animation when releasing the mouse button
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int ticksRemaining) {
		if (shooter instanceof Player player) {
			if (stack.getDamageValue() >= stack.getMaxDamage() - 1)
				return;

			// Add a cooldown so you can't fire rapidly
			player.getCooldowns().addCooldown(this, 5);

			if (!level.isClientSide) {
				Arrow arrow = new Arrow(level, player);
				arrow.tickCount = 35;

				arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1, 1);
				arrow.setBaseDamage(2.5);
				arrow.isNoGravity();

				stack.hurtAndBreak(1, shooter, p -> p.broadcastBreakEvent(shooter.getUsedItemHand()));
				level.addFreshEntity(arrow);

				// Trigger our animation
				// We could trigger this outside of the client-side check if only wanted the animation to play for the shooter
				// But we'll fire it on the server so all nearby players can see it
				triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerLevel)level), "shoot_controller", "shoot");
			}
		}
	}

	// Use vanilla animation to 'pull back' the pistol while charging it
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	// Let's add some ammo text to the tooltip
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("item." + GeckoLib.MOD_ID + ".pistol.ammo",
				stack.getMaxDamage() - stack.getDamageValue() - 1,
				stack.getMaxDamage() - 1)
				.withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}