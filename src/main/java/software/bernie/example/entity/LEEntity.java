package software.bernie.example.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class LEEntity extends PathAwareEntity implements IAnimatable, IAnimationTickable {
    private AnimationFactory factory = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.geoLayerEntity.idle", true));
            return PlayState.CONTINUE;
    }

    public LEEntity(EntityType<? extends PathAwareEntity> type, World worldIn) {
        super(type, worldIn);
    }

	@Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<LEEntity>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
	protected void initGoals() {
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        super.initGoals();
    }

    @Override
    public int tickTimer() {
        return age;
    }
}
