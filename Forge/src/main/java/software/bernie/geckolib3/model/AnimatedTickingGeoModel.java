package software.bernie.geckolib3.model;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.resource.GeckoLibCache;

import java.util.Collections;

public abstract class AnimatedTickingGeoModel<T extends IAnimatable & IAnimationTickable> extends AnimatedGeoModel<T> {
	public AnimatedTickingGeoModel() {
	}

	public boolean isInitialized() {
		return !this.getAnimationProcessor().getModelRendererList().isEmpty();
	}

	@Override
	public void setCustomAnimations(T animatable, int instanceId, AnimationEvent animationEvent) {
		// Each animation has its own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
		if (manager.startTick == -1) {
			manager.startTick = (animatable.tickTimer() + Minecraft.getInstance().getFrameTime());
		}

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			manager.tick = (animatable.tickTimer() + Minecraft.getInstance().getFrameTime());
			double gameTick = manager.tick;
			double deltaTicks = gameTick - lastGameTickTime;
			seekTime += deltaTicks;
			lastGameTickTime = gameTick;
		}

		AnimationEvent<T> predicate;
		if (animationEvent == null) {
			predicate = new AnimationEvent<T>(animatable, 0, 0, 0, false, Collections.emptyList());
		} else {
			predicate = animationEvent;
		}

		predicate.animationTick = seekTime;
		getAnimationProcessor().preAnimationSetup(predicate.getAnimatable(), seekTime);
		if (!this.getAnimationProcessor().getModelRendererList().isEmpty()) {
			getAnimationProcessor().tickAnimation(animatable, instanceId, seekTime, predicate,
					GeckoLibCache.getInstance().parser, shouldCrashOnMissing);
		}

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			codeAnimations(animatable, instanceId, animationEvent);
		}
	}

	public void codeAnimations(T entity, Integer uniqueID, AnimationEvent<?> customPredicate) {

	}
}
