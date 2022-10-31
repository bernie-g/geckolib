package software.bernie.geckolib3.model;

import java.util.Collections;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class AnimatedTickingGeoModel<T extends IAnimatable & IAnimationTickable> extends AnimatedGeoModel<T> {
	public AnimatedTickingGeoModel() {
	}

	public boolean isInitialized() {
		return !this.getAnimationProcessor().getModelRendererList().isEmpty();
	}

	/**
	 * Use {@link IAnimatableModel#setCustomAnimations(Object, int, AnimationEvent)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	@Override
	public void setLivingAnimations(T animatable, Integer instanceId, AnimationEvent animationEvent) {
		this.setCustomAnimations(animatable, instanceId.intValue(), animationEvent);
	}

	@Override
	public void setCustomAnimations(T animatable, int instanceId, @Nullable AnimationEvent animationEvent) {
		// Each animation has its own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
		if (manager.startTick == -1) {
			manager.startTick = (animatable.tickTimer() + MinecraftClient.getInstance().getTickDelta());
		}

		if (!MinecraftClient.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			manager.tick = (animatable.tickTimer() + MinecraftClient.getInstance().getTickDelta());
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

		if (!MinecraftClient.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			codeAnimations(animatable, instanceId, animationEvent);
		}
	}

	public void codeAnimations(T entity, Integer uniqueID, AnimationEvent<?> customPredicate) {

	}
}
