package software.bernie.geckolib3.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib3.core.animation.Animation;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Container object that holds a deserialized map of {@link Animation Animations}.<br>
 * Kept as a unique object so that it can be registered as a {@link software.bernie.geckolib3.util.json.JsonDeserializer deserializer} for {@link com.google.gson.Gson}
 */
public record BakedAnimations(Map<String, Animation> animations) {
	public BakedAnimations() {
		this(new Object2ObjectOpenHashMap<>());
	}

	/**
	 * Gets an {@link Animation} by its name, if present
	 */
	@Nullable
	public Animation getAnimation(String name){
		return animations.get(name);
	}
}
