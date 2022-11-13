package software.bernie.example.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.example.client.renderer.entity.BikeRenderer;
import software.bernie.geckolib3.animatable.GeoEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;

/**
 * Example {@link GeoAnimatable} implementation of an entity
 * @see BikeRenderer
 * @see software.bernie.example.client.model.entity.BikeModel
 */
public class BikeEntity extends Animal implements GeoEntity {
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public BikeEntity(EntityType<? extends Animal> type, Level level) {
		super(type, level);

		this.noCulling = true;
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

	// Add our generic idle animation controller
	@Override
	public void registerControllers(AnimatableManager<?> manager) {
		manager.addAnimationController(DefaultAnimations.genericIdleController(this));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
		return null;
	}
}
