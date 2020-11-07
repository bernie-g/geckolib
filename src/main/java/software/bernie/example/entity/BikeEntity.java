package software.bernie.example.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

public class BikeEntity extends AnimalEntity implements IAnimatable
{
	private AnimationFactory factory = new AnimationFactory(this);

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event)
	{
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bike.idle", true));
		return PlayState.CONTINUE;
	}

	public BikeEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		this.ignoreFrustumCheck = true;
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand)
	{
		if (!this.isBeingRidden())
		{
			player.startRiding(this);
			return super.processInteract(player, hand);
		}
		return super.processInteract(player, hand);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn)
	{
	}

	@Override
	public void travel(Vec3d pos)
	{
		if (this.isAlive())
		{
			if (this.isBeingRidden())
			{
				LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
				this.rotationYaw = livingentity.rotationYaw;
				this.prevRotationYaw = this.rotationYaw;
				this.rotationPitch = livingentity.rotationPitch * 0.5F;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.renderYawOffset = this.rotationYaw;
				this.rotationYawHead = this.renderYawOffset;
				float f = livingentity.moveStrafing * 0.5F;
				float f1 = livingentity.moveForward;
				if (f1 <= 0.0F)
				{
					f1 *= 0.25F;
				}

				this.setAIMoveSpeed(0.3F);
				super.travel(new Vec3d((double) f, pos.y, (double) f1));
			}
		}
	}

	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	@Override
	public boolean canBeSteered() {
		return true;
	}

	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory()
	{
		return this.factory;
	}

	@Nullable
	@Override
	public AgeableEntity createChild(AgeableEntity ageable)
	{
		return null;
	}
}