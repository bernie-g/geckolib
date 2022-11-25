package software.bernie.example.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

/**
 * Example {@link GeoAnimatable} implementation of an entity
 */
public class RaceCarEntity extends Animal implements GeoEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public RaceCarEntity(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	// Let the player ride the entity
	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (!this.isVehicle()) {
			player.startRiding(this);

			return super.mobInteract(player, hand);
		}

		return super.mobInteract(player, hand);
	}

	// Turn off step sounds since it's a bike
	@Override
	protected void playStepSound(BlockPos pos, BlockState block) {}

	// Apply player-controlled movement
	@Override
	public void travel(Vec3 pos) {
		if (this.isAlive()) {
			if (this.isVehicle()) {
				LivingEntity passenger = (LivingEntity)getControllingPassenger();
				this.yRotO = getYRot();
				this.xRotO = getXRot();

				setYRot(passenger.getYRot());
				setXRot(passenger.getXRot() * 0.5f);
				setRot(getYRot(), getXRot());

				this.yBodyRot = this.getYRot();
				this.yHeadRot = this.yBodyRot;
				float x = passenger.xxa * 0.5F;
				float z = passenger.zza;

				if (z <= 0)
					z *= 0.25f;

				this.setSpeed(0.3f);
				super.travel(new Vec3(x, pos.y, z));
			}
		}
	}

	// Get the controlling passenger
	@Nullable
	@Override
	public Entity getControllingPassenger() {
		return getFirstPassenger();
	}

	@Override
	public boolean isControlledByLocalInstance() {
		return true;
	}

	// Adjust the rider's position while riding
	@Override
	public void positionRider(Entity entity) {
		super.positionRider(entity);

		if (entity instanceof LivingEntity passenger) {
			entity.setPos(getX(), getY() - 0.1f, getZ());

			this.xRotO = passenger.xRotO;
		}
	}

	// Add our idle/moving animation controller
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addController(new AnimationController<>(this, "controller", 2, event -> {
			if (event.isMoving() && getControllingPassenger() != null) {
				event.getController().setAnimation(DefaultAnimations.DRIVE);
			}
			else {
				event.getController().setAnimation(DefaultAnimations.IDLE);
			}

			return PlayState.CONTINUE;
			// Handle the sound keyframe that is part of our animation json
		}).setSoundKeyframeHandler(event -> {
			// We don't have a sound for this yet :(
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
		return null;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.5F;
	}
}