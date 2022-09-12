package software.bernie.geckolib3q.file;

import software.bernie.geckolib3.core.builder.Animation;

import java.util.Collection;
import java.util.HashMap;

public class AnimationFile {
	private final HashMap<String, Animation> animations = new HashMap<>();

	public Animation getAnimation(String name) {
		return animations.get(name);
	}

	public Collection<Animation> getAllAnimations() {
		return this.animations.values();
	}

	public void putAnimation(String name, Animation animation) {
		this.animations.put(name, animation);
	}
}
