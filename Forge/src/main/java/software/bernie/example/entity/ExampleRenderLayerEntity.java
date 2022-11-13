package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.animatable.GeoEntity;
import software.bernie.geckolib3.constant.DefaultAnimations;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationController;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.factory.AnimationFactory;
import software.bernie.geckolib3.core.object.PlayState;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Example {@link GeoAnimatable} implementation of an entity that uses a render layer
 */
public class ExampleRenderLayerEntity extends PathfinderMob implements GeoEntity {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public ExampleRenderLayerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    // Add a goal to have the entity look at the player
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        super.registerGoals();
    }

    // Add a generic idle controller, with a 5-tick transition time
	@Override
    public void registerControllers(AnimatableManager<?> manager) {
        manager.addAnimationController(new AnimationController<>(this, "idle", 5, event -> {
            event.getController().setAnimation(DefaultAnimations.IDLE);

            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
