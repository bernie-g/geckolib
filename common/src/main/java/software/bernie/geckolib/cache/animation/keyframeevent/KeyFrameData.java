package software.bernie.geckolib.cache.animation.keyframeevent;

import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/// Base class for custom [Keyframe] events
///
/// @see ParticleKeyframeData
/// @see SoundKeyframeData
public abstract class KeyFrameData {
	private final double animationTime;

	public KeyFrameData(double time) {
		this.animationTime = time;
	}

	/// Gets the time position (in seconds) of the keyframe instruction in its animation
	public double getTime() {
		return this.animationTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		return this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.animationTime);
	}
}
