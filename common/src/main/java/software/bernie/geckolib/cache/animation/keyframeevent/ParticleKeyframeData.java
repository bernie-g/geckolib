package software.bernie.geckolib.cache.animation.keyframeevent;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/// Particle [Keyframe] instruction holder
public class ParticleKeyframeData extends KeyFrameData {
	private final String effect;

	public ParticleKeyframeData(double time, String effect, @Nullable String locator) {
		super(time, locator);

		this.effect = effect;
	}

	/// Gets the effect id given by the [Keyframe] instruction from the `animation.json`
	public String getEffect() {
		return this.effect;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTime(), this.effect, getLocatorName());
	}
}
