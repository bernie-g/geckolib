package software.bernie.geckolib.animatable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;

/**
 * The {@link GeoAnimatable} interface specific to {@link BlockEntity BlockEntities}
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Block-Animations">GeckoLib Wiki - Block Animations</a>
 */
public interface GeoBlockEntity extends GeoAnimatable {
	/**
	 * Get server-synced animation data via its relevant {@link SerializableDataTicket}.
	 * <p>
	 * Should only be used on the <u>client-side</u>
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param dataTicket The data ticket for the data to retrieve
	 * @return The synced data, or null if no data of that type has been synced
	 */
	@ApiStatus.NonExtendable
	default <D> @Nullable D getAnimData(SerializableDataTicket<D> dataTicket) {
		return getAnimatableInstanceCache().getManagerForId(0).getAnimatableData(dataTicket);
	}

	/**
	 * Saves an arbitrary piece of data to this animatable's {@link AnimatableManager}
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param dataTicket The DataTicket to sync the data for
	 * @param data The data to sync
	 */
	@ApiStatus.NonExtendable
	default <D> void setAnimData(SerializableDataTicket<D> dataTicket, D data) {
		BlockEntity blockEntity = (BlockEntity)this;
		Level level = blockEntity.getLevel();

		if (level == null) {
			GeckoLibConstants.LOGGER.error("Attempting to set animation data for BlockEntity too early! Must wait until after the BlockEntity has been set in the world. (" + blockEntity.getClass() + ")");

			return;
		}

		if (level.isClientSide()) {
			getAnimatableInstanceCache().getManagerForId(0).setAnimatableData(dataTicket, data);
		}
		else {
			GeckoLibServices.NETWORK.syncBlockEntityAnimData(blockEntity.getBlockPos(), dataTicket, data, (ServerLevel)level);
		}
	}

	/**
	 * Trigger an animation for this BlockEntity, based on the controller name and animation name
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}
	 */
	@ApiStatus.NonExtendable
	default void triggerAnim(@Nullable String controllerName, String animName) {
		BlockEntity blockEntity = (BlockEntity)this;
		Level level = blockEntity.getLevel();

		if (level == null) {
			GeckoLibConstants.LOGGER.error("Attempting to trigger an animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. (" + blockEntity.getClass() + ")");

			return;
		}

		if (level.isClientSide()) {
			getAnimatableInstanceCache().getManagerForId(0).tryTriggerAnimation(controllerName, animName);
		}
		else {
			GeckoLibServices.NETWORK.triggerBlockEntityAnim(blockEntity.getBlockPos(), (ServerLevel)level, controllerName, animName);
		}
	}

	/**
	 * Stop a previously triggered animation for this BlockEntity for the given controller name and animation name
	 * <p>
	 * This can be fired from either the client or the server, but optimally you would call it from the server
	 * <p>
	 * <b><u>DO NOT OVERRIDE</u></b>
	 *
	 * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search
	 * @param animName The name of the triggered animation to stop, or null to stop any currently playing triggered animation
	 */
	@ApiStatus.NonExtendable
	default void stopTriggeredAnim(@Nullable String controllerName, @Nullable String animName) {
		BlockEntity blockEntity = (BlockEntity)this;
		Level level = blockEntity.getLevel();

		if (level == null) {
			GeckoLibConstants.LOGGER.error("Attempting to stop a triggered animation for a BlockEntity too early! Must wait until after the BlockEntity has been set in the world. (" + blockEntity.getClass() + ")");

			return;
		}

		if (level.isClientSide()) {
			AnimatableManager<GeoAnimatable> animatableManager = getAnimatableInstanceCache().getManagerForId(0);

			if (animatableManager == null)
				return;

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
