package software.bernie.geckolib.example.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.animation.controller.EntityAnimationController;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;

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
	public PassiveEntity createChild(PassiveEntity ageable) {
		return null;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.eatGrassGoal = new EatGrassGoal(this);
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25D));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1D));
		this.goalSelector.add(5, this.eatGrassGoal);
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void mobTick() {
		this.exampleTimer = this.eatGrassGoal.getTimer();
		super.mobTick();
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.exampleTimer = Math.max(0, this.exampleTimer - 1);
		}
		super.tickMovement();
	}

	public static DefaultAttributeContainer.Builder createColorfulPigAttributes() {
		return AscendedLegfishEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D);
	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte id) {
		if (id == 10) {
			this.exampleTimer = 40;
		} else {
			super.handleStatus(id);
		}

	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public float getHeadRotationAngleX(float p_70890_1_) {
		if (this.exampleTimer > 4 && this.exampleTimer <= 36) {
			float f = ((float) (this.exampleTimer - 4) - p_70890_1_) / 32.0F;
			return ((float) Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
		} else {
			return this.exampleTimer > 0 ? ((float) Math.PI / 5F) : this.pitch * ((float) Math.PI / 180F);
		}
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningBolt) {
		this.setGlowing(true);
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

