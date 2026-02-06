package software.bernie.geckolib.cache.animation.keyframeevent;

import software.bernie.geckolib.cache.animation.Keyframe;

import java.util.Objects;

/// Custom instruction [Keyframe] instruction holder
public class CustomInstructionKeyframeData extends KeyFrameData {
	private final String instructions;

	public CustomInstructionKeyframeData(double time, String instructions) {
		super(time);

		this.instructions = instructions;
	}

	/// Gets the instructions string given by the [Keyframe] instruction from the `animation.json`
	public String getInstructions() {
		return this.instructions;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTime(), this.instructions);
	}
}
