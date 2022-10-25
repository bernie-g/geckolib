package software.bernie.geckolib3.file;

import java.util.Collection;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.core.builder.Animation;

public record AnimationFile(Map<String, Animation> animations) {
	public AnimationFile() {
		this(new Object2ObjectOpenHashMap<>());
	}

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
