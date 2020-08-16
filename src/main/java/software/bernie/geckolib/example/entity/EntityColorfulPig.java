package software.bernie.geckolib.example.entity;


import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

import javax.annotation.Nullable;

public class EntityColorfulPig extends AnimalEntity implements IAnimatedEntity
{

	private EatGrassGoal eatGrassGoal;
	private int exampleTimer;
	private EntityAnimationManager manager = new EntityAnimationManager();
	private EntityAnimationController controller = new EntityAnimationController(this, "moveController", 20,
			this::animationPredicate);

	public EntityColorfulPig(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
		registerAnimationControllers();
	}

	private void registerAnimationControllers() {
		manager.addAnimationController(controller);
	}


	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.eatGrassGoal = new EatGrassGoal(this);
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
		this.goalSelector.addGoal(5, this.eatGrassGoal);
		this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}

	@Override
	protected void updateAITasks() {
		this.exampleTimer = this.eatGrassGoal.getEatingGrassTimer();
		super.updateAITasks();
	}

	@Nullable
	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
	{
		return null;
	}

	@Override
	public void livingTick() {
		if (this.world.isRemote) {
			this.exampleTimer = Math.max(0, this.exampleTimer - 1);
		}
		super.livingTick();
	}



	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 10) {
			this.exampleTimer = 40;
		} else {
			super.handleStatusUpdate(id);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationPointY(float p_70894_1_) {
		if (this.exampleTimer <= 0) {
			return 0.0F;
		} else if (this.exampleTimer >= 4 && this.exampleTimer <= 36) {
			return 1.0F;
		} else {
			return this.exampleTimer < 4 ? ((float) this.exampleTimer - p_70894_1_) / 4.0F
					: -((float) (this.exampleTimer - 40) - p_70894_1_) / 4.0F;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationAngleX(float p_70890_1_) {
		if (this.exampleTimer > 4 && this.exampleTimer <= 36) {
			float f = ((float) (this.exampleTimer - 4) - p_70890_1_) / 32.0F;
			return ((float) Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
		} else {
			return this.exampleTimer > 0 ? ((float) Math.PI / 5F) : this.rotationPitch * ((float) Math.PI / 180F);
		}
	}


	private <E extends EntityColorfulPig> boolean animationPredicate(AnimationTestEvent<E> event) {
		if (event.isWalking()) {
			controller.setAnimation(new AnimationBuilder().addAnimation("animation.turtywurty.move"));
			return true;
		}
		return false;
	}

	@Override
	public EntityAnimationManager getAnimationManager() {
		return manager;
	}
}

