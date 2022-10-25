package software.bernie.example.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class CarEntity extends Animal implements IAnimatable {
	public AnimationFactory factory = GeckoLibUtil.createFactory(this);

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		if (event.isMoving() && this.getFirstPassenger() != null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("moving", EDefaultLoopTypes.LOOP));
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", EDefaultLoopTypes.LOOP));
		}
		return PlayState.CONTINUE;
	}

	public CarEntity(EntityType<? extends Animal> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (!this.isVehicle()) {
			player.startRiding(this);
			return super.mobInteract(player, hand);
		}
		return super.mobInteract(player, hand);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
	}

	@Override
	public void travel(Vec3 pos) {
		if (this.isAlive()) {
			if (this.isVehicle()) {
				LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
				this.setYRot(livingentity.getYRot());
				this.yRotO = this.getYRot();
				this.setXRot(livingentity.getXRot() * 0.5F);
				this.setRot(this.getYRot(), this.getXRot());
				this.yBodyRot = this.getYRot();
				this.yHeadRot = this.yBodyRot;
				float f = livingentity.xxa * 0.5F;
				float f1 = livingentity.zza;
				if (f1 <= 0.0F) {
					f1 *= 0.25F;
				}

				this.setSpeed(0.3F);
				super.travel(new Vec3((double) f, pos.y, (double) f1));
			}
		}
	}

	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	@Override
	public boolean hasExactlyOnePlayerPassenger() {
		return true;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<CarEntity>(this, "controller", 2, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
		return null;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 0.5F;
	}

	@Override
	public void positionRider(Entity passenger) {
		super.positionRider(passenger);
		if (passenger instanceof LivingEntity) {
			LivingEntity mob = (LivingEntity) passenger;
			passenger.setPos(this.getX(), this.getY() - 0.1f, this.getZ());
			this.xRotO = mob.xRotO;
		}
	}
}