package com.geckolib.animatable;

import com.geckolib.GeckoLibConstants;
import com.geckolib.GeckoLibServices;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// The [GeoAnimatable] interface specific to [BlockEntities][BlockEntity]
///
/// @see <a href="https://github.com/bernie-g/geckolib/wiki/Block-Animations">GeckoLib Wiki - Block Animations</a>
public interface GeoBlockEntity extends GeoAnimatable {
	/// Trigger an animation for this BlockEntity, based on the controller name and animation name
	///
	/// **<u>DO NOT OVERRIDE</u>**
	///
	/// @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	/// @param animName The name of animation to trigger. This needs to have been registered with the controller via [AnimationController.triggerableAnim][AnimationController#triggerableAnim]
	@ApiStatus.NonExtendable
	default void triggerAnim(@Nullable String controllerName, String animName) {
		final BlockEntity blockEntity = (BlockEntity)this;
		final Level level = blockEntity.getLevel();

		if (level == null) {
            GeckoLibConstants.LOGGER.error("Attempting to trigger an animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. ({})", blockEntity.getClass());

			return;
		}

		if (level.isClientSide()) {
			AnimatableManager<?> manager = getAnimatableInstanceCache().getManagerForId(0);

			if (controllerName == null) {
				manager.tryTriggerAnimation(animName);
			}
			else {
				manager.tryTriggerAnimation(controllerName, animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.triggerBlockEntityAnim(blockEntity.getBlockPos(), (ServerLevel)level, controllerName, animName);
		}
	}

	/// Stop a previously triggered animation for this BlockEntity for the given controller name and animation name
	///
	/// This can be fired from either the client or the server, but optimally you would call it from the server
	///
	/// **<u>DO NOT OVERRIDE</u>**
	///
	/// @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	/// @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
	@ApiStatus.NonExtendable
	default void stopTriggeredAnim(@Nullable String controllerName, @Nullable String animName) {
		BlockEntity blockEntity = (BlockEntity)this;
		Level level = blockEntity.getLevel();

		if (level == null) {
            GeckoLibConstants.LOGGER.error("Attempting to stop a triggered animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. ({})", blockEntity.getClass());

			return;
		}

		if (level.isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(0);

			if (controllerName != null) {
				animatableManager.stopTriggeredAnimation(controllerName, animName);
			}
			else {
				animatableManager.stopTriggeredAnimation(animName);
			}
		}
		else {
			GeckoLibServices.NETWORK.stopTriggeredBlockEntityAnim(blockEntity.getBlockPos(), (ServerLevel)level, controllerName, animName);
		}
	}
}
