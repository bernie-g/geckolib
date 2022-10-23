package software.bernie.geckolib3.file;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.core.builder.Animation;

import java.util.Collection;
import java.util.Map;

public class AnimationFile {
	private Map<String, Animation> animations = new Object2ObjectOpenHashMap<>();

	public Animation getAnimation(String name) {
		return animations.get(name);
	}

	public void putAnimation(String name, Animation animation) {
		this.animations.put(name, animation);
	}

	public Collection<Animation> getAllAnimations() {
		return this.animations.values();
	}
}
