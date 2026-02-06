package software.bernie.geckolib.cache.animation.keyframeevent;

import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/// Particle [Keyframe] instruction holder
public class ParticleKeyframeData extends KeyFrameData {
	private final String effect;
	private final String locator;
	private final String script;

	public ParticleKeyframeData(double time, String effect, String locator, String script) {
		super(time);

		this.script = script;
		this.locator = locator;
		this.effect = effect;
	}

	/// Gets the effect id given by the [Keyframe] instruction from the `animation.json`
	public String getEffect() {
		return this.effect;
	}

	/// Gets the locator string given by the [Keyframe] instruction from the `animation.json`
	public String getLocator() {
		return this.locator;
	}

	/// Gets the script string given by the [Keyframe] instruction from the `animation.json`
	public String script() {
		return this.script;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTime(), this.effect, this.locator, this.script);
	}
}
