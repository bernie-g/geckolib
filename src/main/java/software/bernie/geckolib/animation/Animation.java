package software.bernie.geckolib.animation;

import org.antlr.v4.runtime.misc.NotNull;
import software.bernie.geckolib.animation.keyframe.*;

import java.util.List;

public class Animation
{
	public String animationName;
	public float animationLength;

	public List<BoneAnimation> boneAnimations;
	public List<SoundEffectKeyFrame> rotationKeyFrames;
	public List<ParticleEffectKeyFrame> particleEffectKeyFrames;
	public List<CustomInstructionKeyFrame> customInstructionKeyFrames;
}
