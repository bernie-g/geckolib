package software.bernie.geckolib.core;

import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.core.processor.IBone;

public interface IAnimatableModel<E> {
    default float getCurrentTick() {
        return (System.nanoTime() / 1000000L / 50f);
    }

    default void setLivingAnimations(E entity, Integer uniqueID) {
        this.setLivingAnimations(entity, uniqueID, null);
    }

    void setLivingAnimations(E entity, Integer uniqueID, AnimationEvent customPredicate);

    AnimationProcessor getAnimationProcessor();

    Animation getAnimation(String name, IAnimatable animatable);

    /**
     * Gets a bone by name.
     *
     * @param boneName The bone name
     * @return the bone
     */
    default IBone getBone(String boneName) {
        IBone bone = this.getAnimationProcessor().getBone(boneName);
        if (bone == null) {
            throw new RuntimeException("Could not find bone: " + boneName);
        }
        return bone;
    }

    void setMolangQueries(IAnimatable animatable, double currentTick);
}
