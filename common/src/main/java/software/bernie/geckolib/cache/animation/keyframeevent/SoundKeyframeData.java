package software.bernie.geckolib.cache.animation.keyframeevent;

import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/// Sound [Keyframe] instruction holder
public class SoundKeyframeData extends KeyFrameData {
	private final String sound;

	public SoundKeyframeData(double time, String sound, @Nullable String locator) {
		super(time, locator);

		this.sound = sound;
	}

	/// Gets the sound data given by the [Keyframe] instruction from the `animation.json`
	public String getSound() {
		return this.sound;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTime(), this.sound);
	}
}
