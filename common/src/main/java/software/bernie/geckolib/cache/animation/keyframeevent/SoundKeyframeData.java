package software.bernie.geckolib.cache.animation.keyframeevent;

import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/**
 * Sound {@link Keyframe} instruction holder
 */
public class SoundKeyframeData extends KeyFrameData {
	private final String sound;

	public SoundKeyframeData(double time, String sound) {
		super(time);

		this.sound = sound;
	}

	/**
	 * Gets the sound data given by the {@link Keyframe} instruction from the {@code animation.json}
	 */
	public String getSound() {
		return this.sound;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTime(), this.sound);
	}
}
