package software.bernie.example.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BikeEntity extends AnimalEntity implements IAnimatable {
	private final AnimationFactory factory = new AnimationFactory(this);

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bike.idle", true));
		return PlayState.CONTINUE;
	}

	public BikeEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
		this.ignoreCameraFrustum = true;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (!this.hasPassengers()) {
			player.startRiding(this);
			return super.interactMob(player, hand);
		}
		return super.interactMob(player, hand);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
	}

	@Override
	public void travel(Vec3d pos) {
		if (this.isAlive()) {
			if (this.hasPassengers()) {
				LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
				this.yaw = livingentity.getYaw();
				this.prevYaw = this.getYaw();
				this.pitch = livingentity.getPitch() * 0.5F;
				this.setRotation(this.getYaw(), this.getPitch());
				this.bodyYaw = this.getYaw();
				this.headYaw = this.bodyYaw;
				float f = livingentity.sidewaysSpeed * 0.5F;
				float f1 = livingentity.forwardSpeed;
				if (f1 <= 0.0F) {
					f1 *= 0.25F;
				}

				this.setMovementSpeed(0.3F);
				super.travel(new Vec3d(f, pos.y, f1));
			}
		}
	}

	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
	}

	@Override
	public boolean canBeControlledByRider() {
		return true;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<BikeEntity>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}
}