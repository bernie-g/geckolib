package software.bernie.geckolib.cache.animation.keyframeevent;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/// Base class for custom [Keyframe] events
///
/// @see ParticleKeyframeData
/// @see SoundKeyframeData
public abstract class KeyFrameData {
	private final double animationTime;
	private final @Nullable String locatorName;

	public KeyFrameData(double time, @Nullable String locatorName) {
		this.animationTime = time;
		this.locatorName = locatorName;
	}

	/// @return The time position (in seconds) of the keyframe instruction in its animation
	public double getTime() {
		return this.animationTime;
	}

	/// Gets the name of the locator this keyframe instruction targets, if any
	///
	/// @return The name of the locator, or null if not targeted at anything
	public @Nullable String getLocatorName() {
		return this.locatorName;
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
